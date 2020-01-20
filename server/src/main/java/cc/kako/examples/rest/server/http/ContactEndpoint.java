package cc.kako.examples.rest.server.http;

import cc.kako.examples.rest.api.data.ContactProvider;
import cc.kako.examples.rest.api.dto.Contact;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogService;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Component(service = ContactEndpoint.class, property = { "osgi.jaxrs.resource=true" })
@Path("/contact")
public class ContactEndpoint {
    @Reference
    LogService logger;

    @Reference
    ContactProvider contactProvider;

    @Activate
    public void activate() {
        logger.log(LogService.LOG_INFO, "** ContactEndpoint activated **");
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Contact> readAll() {
        return contactProvider.readAll(e -> { });
    }

    @GET
    @Path("search")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Contact> search(@QueryParam("query") final String query) {
        return contactProvider.search(query, e -> { });
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Contact read(@PathParam("id") final Long id) {
        return contactProvider.read(id, e -> { })
                .orElseThrow(() -> new NotFoundException());
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Contact create(final Contact entry) {
        return contactProvider.create(entry, e -> { })
                .orElseThrow(() -> new NotFoundException());
    }

    @DELETE
    @Path("{id}")
    public void create(@PathParam("id") final Long id) {
        contactProvider.delete(id, e -> { });
    }
}
