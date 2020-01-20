package cc.kako.examples.rest.server.data;

import cc.kako.examples.rest.api.data.ContactProvider;
import cc.kako.examples.rest.api.dto.Contact;
import org.apache.aries.jpa.template.JpaTemplate;
import org.apache.aries.jpa.template.TransactionType;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.osgi.framework.ServiceException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.log.LogService;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Component
public class JpaContactProvider implements ContactProvider {
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

    private AtomicBoolean ready = new AtomicBoolean();

    @Deactivate
    public void deactivate() {
        ready.set(false);
    }

    public Optional<Contact> read(final Long id, final Consumer<Exception> onError) {
        if (!ready.get()) {
            onError.accept(new ServiceException("Not ready"));
            return Optional.empty();
        }

        return Optional.ofNullable(jpaTemplate.txExpr(TransactionType.Supports,
                entityManager -> entityManager.find(Contact.class, id)));
    }

    public List<Contact> readAll(final Consumer<Exception> onError) {
        return jpaTemplate.txExpr(TransactionType.Supports,
                entityManager -> entityManager.createQuery("SELECT c FROM Contact c", Contact.class).getResultList());
    }

    public Optional<Contact> create(final Contact entry, final Consumer<Exception> onError) {
        jpaTemplate.tx(TransactionType.RequiresNew, entityManager -> {
            entityManager.persist(entry);
            entityManager.flush();
        });

        return Optional.of(entry);
    }

    public Optional<Contact> update(final Long id, final Contact entry, final Consumer<Exception> onError) {
        return Optional.empty();
    }

    public void delete(final Long id, final Consumer<Exception> onError) {
        jpaTemplate.tx(TransactionType.RequiresNew, entityManager -> {
            Contact contact = entityManager.find(Contact.class, id);
            if (contact !=  null) {
                entityManager.remove(contact);
            }
        });
    }

    public List<Contact> search(final String query, final Consumer<Exception> onError) {
        // XXX: todo - minimum query length suggested
        // XXX: limit
        return jpaTemplate.txExpr(TransactionType.Supports, em -> {
            FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);
            em.getTransaction().begin();

            // create native Lucene query unsing the query DSL
            QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Contact.class).get();

            // Simple look ahead query with wildcard
            org.apache.lucene.search.Query luceneQuery = qb.keyword()
                    .wildcard()
                    .onFields("name", "emailAddress")
                    .matching(query + "*")
                    .createQuery();

            // wrap Lucene query in a javax.persistence.Query
            javax.persistence.Query jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, Contact.class);

            @SuppressWarnings("unchecked")
            List<Contact> result = (List<Contact>) jpaQuery.getResultList();

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
