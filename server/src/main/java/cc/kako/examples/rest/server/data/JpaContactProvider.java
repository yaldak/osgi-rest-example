package cc.kako.examples.rest.server.data;

import cc.kako.examples.rest.api.data.ContactProvider;
import cc.kako.examples.rest.api.dto.Contact;
import org.apache.aries.jpa.template.JpaTemplate;
import org.apache.aries.jpa.template.TransactionType;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.dsl.TermTermination;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.log.LogService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

@Component
public class JpaContactProvider implements ContactProvider {
    private static final String E_NOT_READY = "Not ready";

    @Reference
    LogService logger;

    @Reference(
            target="(osgi.unit.name=contact-pu)",
            policy=ReferencePolicy.DYNAMIC,
            cardinality=ReferenceCardinality.OPTIONAL,
            bind="bindJpaTemplate",
            unbind="unbindJpaTemplate"
    )
    private volatile JpaTemplate jpaTemplate;

    /* Used to control integrity in the event of Data factory SCR bind/unbind */
    private AtomicBoolean ready = new AtomicBoolean();

    @Activate
    public void activate(final BundleContext context) {
        logger.log(LogService.LOG_INFO, "** JpaContactProvider activated **");
    }

    @Deactivate
    public void deactivate() {
        ready.set(false);
    }

    @Override
    public Optional<Contact> read(final Long id, final Consumer<Exception> onError) {
        if (!ready.get()) {
            onError.accept(new ServiceException(E_NOT_READY));
            return Optional.empty();
        }

        return Optional.ofNullable(jpaTemplate.txExpr(TransactionType.Supports,
                entityManager -> entityManager.find(Contact.class, id)));
    }

    @Override
    public List<Contact> readAll(final Consumer<Exception> onError) {
        if (!ready.get()) {
            onError.accept(new ServiceException(E_NOT_READY));
            return Collections.emptyList();
        }

        return jpaTemplate.txExpr(TransactionType.Supports,
                entityManager -> entityManager.createQuery("SELECT c FROM Contact c", Contact.class).getResultList());
    }

    @Override
    public Optional<Contact> create(final Contact entry, final Consumer<Exception> onError) {
        if (!ready.get()) {
            onError.accept(new ServiceException(E_NOT_READY));
            return Optional.empty();
        }

        jpaTemplate.tx(TransactionType.RequiresNew, entityManager -> {
            entityManager.persist(entry);
            entityManager.flush();
        });

        return Optional.of(entry);
    }

    @Override
    public Optional<Contact> update(final Long id, final Contact entry, final Consumer<Exception> onError) {
        if (!ready.get()) {
            onError.accept(new ServiceException(E_NOT_READY));
            return Optional.empty();
        }

        if (!id.equals(entry.getId())) {
            onError.accept(new IllegalArgumentException("id does not match entry id"));
            return Optional.empty();
        }

        jpaTemplate.tx(TransactionType.RequiresNew, entityManager -> {
            entityManager.merge(entry);
            entityManager.flush();
        });

        return Optional.of(entry);
    }

    @Override
    public void delete(final Long id, final Consumer<Exception> onError) {
        if (!ready.get()) {
            onError.accept(new ServiceException(E_NOT_READY));
            return;
        }

        jpaTemplate.tx(TransactionType.RequiresNew, entityManager -> {
            Contact contact = entityManager.find(Contact.class, id);
            if (contact !=  null) {
                entityManager.remove(contact);
            }
        });
    }

    @Override
    public List<Contact> searchByEmailOrPhone(final String queryText, final Consumer<Exception> onError) {
        /* WISH(yalda): This should have throttling in the form of result limits and query minimum */
        /* Simple lookahead with wildcard */
        return normalizeQueryText(queryText, onError)
                .map(q -> searchInternal(qb -> qb.keyword()
                        .wildcard()
                        .onFields("emailAddress", "phoneNumberPersonal", "phoneNumberWork")
                        .matching(q), onError))
                .orElse(Collections.emptyList());
    }

    @Override
    public List<Contact> searchByCity(final String queryText, final Consumer<Exception> onError) {
        return normalizeQueryText(queryText, onError)
                .map(q -> searchInternal(qb -> qb.keyword()
                        .wildcard()
                        .onFields("address.city")
                        .matching(q), onError))
                .orElse(Collections.emptyList());
    }

    @Override
    public List<Contact> searchByState(final String queryText, final Consumer<Exception> onError) {
        return normalizeQueryText(queryText, onError)
                .map(q -> searchInternal(qb -> qb.keyword()
                        .wildcard()
                        .onFields("address.state")
                        .matching(q + "*"), onError))
                .orElse(Collections.emptyList());
    }

    private Optional<String> normalizeQueryText(final String queryText, final Consumer<Exception> onError) {
        return Optional.of(queryText)
                .map(String::trim)
                .filter(s -> s.length() > 1)
                .map(String::toLowerCase);
    }

    private List<Contact> searchInternal(final Function<QueryBuilder, TermTermination> withQueryBuilder,
            final Consumer<Exception> onError) {
        if (!ready.get()) {
            onError.accept(new ServiceException(E_NOT_READY));
            return Collections.emptyList();
        }

        return jpaTemplate.txExpr(TransactionType.Supports, entityManager -> {
            FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
            entityManager.getTransaction().begin();

            /* Create native Lucene query using the query DSL */
            QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
                    .buildQueryBuilder()
                    .forEntity(Contact.class)
                    .get();

            org.apache.lucene.search.Query luceneQuery = withQueryBuilder.apply(queryBuilder).createQuery();

            /* Wrap Lucene query in a javax.persistence.Query */
            javax.persistence.Query jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, Contact.class);

            @SuppressWarnings("unchecked")
            List<Contact> result = (List<Contact>) jpaQuery.getResultList();

            entityManager.getTransaction().commit();

            return result;
        });
    }

    private void bindJpaTemplate(final JpaTemplate jpaTemplate) {
        ready.set(false);
        this.jpaTemplate = jpaTemplate;
        ready.set(true);
    }

    private void unbindJpaTemplate(final JpaTemplate jpa) {
        ready.set(false);
        this.jpaTemplate = null;
    }
}
