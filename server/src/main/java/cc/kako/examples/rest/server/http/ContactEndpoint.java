package cc.kako.examples.rest.server.http;

import cc.kako.examples.rest.api.data.ContactProvider;
import cc.kako.examples.rest.api.dto.Contact;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogService;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
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
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Contact read(@PathParam("id") final Long id) {
        return contactProvider.read(id, e -> { }).orElseThrow(NotFoundException::new);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Contact create(final Contact entry) {
        return contactProvider.create(entry, e -> { }).orElseThrow(BadRequestException::new);
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Contact update(@PathParam("id") final Long id, final Contact entry) {
        return contactProvider.update(id, entry, System.out::println).orElseThrow(BadRequestException::new);
    }

    @DELETE
    @Path("{id}")
    public void delete(@PathParam("id") final Long id) {
        contactProvider.delete(id, e -> { });
    }

    @GET
    @Path("searchByNameOrEmail")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Contact> searchByNameOrEmail(@QueryParam("queryText") final String queryText) {
        return contactProvider.searchByNameOrEmail(queryText, e -> { });
    }

    @GET
    @Path("searchByState")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Contact> searchByState(@QueryParam("queryText") final String queryText) {
        //return contactProvider.searchByState(queryText, e -> { });
        return Collections.emptyList();
    }

    @GET
    @Path("searchByCity")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Contact> searchByCity(@QueryParam("queryText") final String queryText) {
        //return contactProvider.searchByState(queryText, e -> { });
        return Collections.emptyList();
    }

    @GET
    @Path("{id}/photo")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public byte[] readPhoto(@PathParam("id") final Long id) {
        return contactProvider.read(id, e -> { })
                .map(Contact::getPhoto)
                .orElseThrow(NotFoundException::new);
    }
/*
    @POST
    @Path("{id}/photo")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public void writePhoto(@PathParam("id") final Long id,
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }

            contactProvider.read(id, e -> { })
                    .map(c -> { c.setPhoto(out.toByteArray()); return c; })
                    .ifPresent(c -> contactProvider.update(id, c, System.out::println));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
 */
}
