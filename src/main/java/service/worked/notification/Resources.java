package service.worked.notification;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Path("keys")
public class Resources {

    @Inject
    Key key;

    @GET
    @Path("public")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String>  getPublicKey() throws IOException {
        System.out.println("SE MANDO LA KEY");
        Map<String, String> response = new HashMap<>();
        response.put("key", new String(key.getPublicKey()));
        return response;
    }
}
