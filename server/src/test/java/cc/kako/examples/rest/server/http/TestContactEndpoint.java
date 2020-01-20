package cc.kako.examples.rest.server.http;

import cc.kako.examples.rest.api.data.ContactProvider;
import cc.kako.examples.rest.api.dto.Contact;
import cc.kako.examples.rest.server.http.ContactEndpoint;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestContactEndpoint {
    private ContactEndpoint endpoint;

    private static final List<Contact> CONTACT_LIST = Collections.emptyList();

    @Before
    public void setUp() {
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
}
