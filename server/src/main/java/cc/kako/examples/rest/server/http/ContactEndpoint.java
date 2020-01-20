package cc.kako.examples.rest.server.http;

import cc.kako.examples.rest.api.data.ContactProvider;
import cc.kako.examples.rest.api.util.Try;
import cc.kako.examples.rest.api.dto.Contact;
import org.osgi.framework.BundleContext;
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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.List;

@Component(service = ContactEndpoint.class, property = { "osgi.jaxrs.resource=true" })
@Path("/contact")
public class ContactEndpoint {
    @Reference
    LogService logger;

    @Reference
    ContactProvider contactProvider;

    private URL defaultImageUrl;

    @Activate
    public void activate(final BundleContext context) {
        logger.log(LogService.LOG_INFO, "** ContactEndpoint activated **");

        defaultImageUrl = context.getBundle().getResource("/assets/default-image.png");
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Contact> readAll() {
        return contactProvider.readAll(this::onError);
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Contact read(@PathParam("id") final Long id) {
        return contactProvider.read(id, this::onError).orElseThrow(NotFoundException::new);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Contact create(final Contact entry) {
        return contactProvider.create(entry, this::onError).orElseThrow(BadRequestException::new);
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Contact update(@PathParam("id") final Long id, final Contact entry) {
        return contactProvider.update(id, entry, this::onError).orElseThrow(BadRequestException::new);
    }

    @DELETE
    @Path("{id}")
    public void delete(@PathParam("id") final Long id) {
        contactProvider.delete(id, this::onError);
    }

    @GET
    @Path("search/emailOrPhone")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Contact> searchByEmailOrPhone(@QueryParam("q") final String queryText) {
        return contactProvider.searchByEmailOrPhone(queryText, this::onError);
    }

    @GET
    @Path("search/city")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Contact> searchByCity(@QueryParam("q") final String queryText) {
        return contactProvider.searchByCity(queryText, this::onError);
    }

    @GET
    @Path("search/state")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Contact> searchByState(@QueryParam("q") final String queryText) {
        return contactProvider.searchByState(queryText, this::onError);
    }

    @GET
    @Path("{id}/photo")
    @Produces("image/png")
    public byte[] readPhoto(@PathParam("id") final Long id) {
        return contactProvider.read(id, this::onError)
                .map(Contact::getPhoto)
                .orElseThrow(NotFoundException::new);
    }

    @POST
    @Path("{id}/photo")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public void writePhoto(@PathParam("id") final Long id) {
        /* TODO(ykako): See README.md#The Multipart Problem */
        Try.run(() -> {
            byte[] data = readFully(defaultImageUrl.openStream());

            contactProvider.read(id, e -> { })
                    .ifPresent(c -> {
                        c.setPhoto(data);

                        contactProvider.update(id, c, this::onError);
                    });
        }, this::onError);
    }

    @DELETE
    @Path("{id}/photo")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public void deletePhoto(@PathParam("id") final Long id) {
        contactProvider.read(id, e -> { })
                .ifPresent(c -> {
                    c.setPhoto(null);

                    contactProvider.update(id, c, this::onError);
                });
    }

    private void onError(final Exception e) {
        logger.log(LogService.LOG_ERROR, "internal error", e);
    }

    /**
     * Read an InputStream into a primitive byte array with an 8K pre-allocated buffer.
     * We slurp the stream this way because NIO Files & Path do not understand OSGi bundle URIs.
     *
     * @param input
     * @return
     * @throws IOException
     */
    private static byte[] readFully(final InputStream input) throws IOException {
        ReadableByteChannel c = Channels.newChannel(input);
        ByteBuffer buf = ByteBuffer.allocate(8192);
        c.read(buf);

        return buf.array();
    }
}
