package cc.kako.examples.rest.api.data;

import cc.kako.examples.rest.api.dto.Contact;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public interface ContactProvider {
    Optional<Contact> read(final String id, final Consumer<Exception> onError);

    List<Contact> readAll(final Consumer<Exception> onError);

    Optional<Contact> create(final Consumer<Exception> onError);

    Optional<Contact> update(final String id, final Contact entry, final Consumer<Exception> onError);

    void delete(final String id, final Consumer<Exception> onError);
}
