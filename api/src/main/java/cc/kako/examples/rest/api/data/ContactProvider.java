package cc.kako.examples.rest.api.data;

import cc.kako.examples.rest.api.dto.Contact;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public interface ContactProvider {
    Optional<Contact> read(Long id, Consumer<Exception> onError);

    List<Contact> readAll(Consumer<Exception> onError);

    Optional<Contact> create(Contact entry, Consumer<Exception> onError);

    Optional<Contact> update(Long id, Contact entry, Consumer<Exception> onError);

    void delete(Long id, Consumer<Exception> onError);

    List<Contact> searchByEmailOrPhone(String queryText, Consumer<Exception> onError);

    List<Contact> searchByCity(String queryText, Consumer<Exception> onError);

    List<Contact> searchByState(String queryText, Consumer<Exception> onError);
}
