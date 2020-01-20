package cc.kako.examples.rest.server.data;

import cc.kako.examples.rest.api.data.ContactProvider;
import cc.kako.examples.rest.api.dto.Contact;
import org.apache.aries.jpa.template.JpaTemplate;
import org.apache.aries.jpa.template.TransactionType;
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

    private void bindJpaTemplate(final JpaTemplate jpaTemplate) {
        System.out.println("binding JpaTemplate");

        this.jpaTemplate = jpaTemplate;
    }

    private void unbindJpaTemplate(final JpaTemplate jpa) {
        System.out.println("unbinding JpaTemplate");

        this.jpaTemplate = null;
    }

    @Activate
    public void activate() {
        System.out.println("*** JpaContactProvider activated ***");

       // jpaTemplate.txExpr(TransactionType.Supports,
       //         entityManager -> entityManager.createQuery("SELECT b FROM Contact b", Contact.class).getResultList())
       // .forEach(System.out::println);
    }

    @Deactivate
    public void deactivate() {
        System.out.println("*** JpaContactProvider deactivated ***");
    }

    public Optional<Contact> read(final Long id, final Consumer<Exception> onError) {
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

        return Optional.empty(); // just for now
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
}
