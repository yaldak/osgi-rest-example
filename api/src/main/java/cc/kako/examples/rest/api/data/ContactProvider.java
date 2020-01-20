package cc.kako.examples.rest.api.data;

import cc.kako.examples.rest.api.dto.Contact;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public interface ContactProvider {
    Optional<Contact> read(final Long id, final Consumer<Exception> onError);

    List<Contact> readAll(final Consumer<Exception> onError);

    Optional<Contact> create(final Contact entry, final Consumer<Exception> onError);

    Optional<Contact> update(final Long id, final Contact entry, final Consumer<Exception> onError);

    void delete(final Long id, final Consumer<Exception> onError);

    List<Contact> searchByNameOrEmail(final String queryText, final Consumer<Exception> onError);
}
