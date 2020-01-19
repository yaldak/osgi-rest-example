package cc.kako.examples.rest.server.data;

import cc.kako.examples.rest.api.data.ContactProvider;
import cc.kako.examples.rest.api.dto.Contact;
import org.apache.aries.jpa.template.JpaTemplate;
import org.apache.aries.jpa.template.TransactionType;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Component
public class JpaContactProvider implements ContactProvider {
    @Reference(target = "(osgi.unit.name=contact-pu)")
    private JpaTemplate jpaTemplate;

    @Activate
    public void activate() {
        System.out.println("--------- HELLO  ----------");

       // jpaTemplate.txExpr(TransactionType.Supports,
       //         entityManager -> entityManager.createQuery("SELECT b FROM Contact b", Contact.class).getResultList())
       // .forEach(System.out::println);
    }

    @Deactivate
    public void deactivate() {
    }

    public Optional<Contact> read(final String id, final Consumer<Exception> onError) {
        return Optional.empty();
    }

    public List<Contact> readAll(final Consumer<Exception> onError) {
        return Collections.emptyList();
    }

    public Optional<Contact> create(final Consumer<Exception> onError) {
        return Optional.empty();
    }

    public Optional<Contact> update(final String id, final Contact entry, final Consumer<Exception> onError) {
        return Optional.empty();
    }

    public void delete(final String id, final Consumer<Exception> onError) {

    }
}
