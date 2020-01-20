package cc.kako.examples.rest.server.http;

import cc.kako.examples.rest.api.data.ContactProvider;
import cc.kako.examples.rest.api.dto.Contact;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestContactEndpoint {
    private ContactEndpoint endpoint;

    private static final List<Contact> CONTACT_LIST = new ArrayList<>();

    @Before
    public void setUp() {
        populateContactList(CONTACT_LIST);

        ContactEndpoint endpoint = new ContactEndpoint();

        ContactProvider contactProvider = mock(ContactProvider.class);

        when(contactProvider.readAll(any())).thenReturn(CONTACT_LIST);

        endpoint.contactProvider = contactProvider;

        this.endpoint = endpoint;
    }

    @Test
    public void testReadAll() {
        assertEquals(this.endpoint.readAll().size(), CONTACT_LIST.size());
    }

    private static void populateContactList(final List<Contact> list) {
        //list.add(generateContact())
    }

    private static Contact generateContact() {
        return null;
    }
}
