package service.worked.notification.resources;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.scheduler.Scheduled;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jose4j.lang.JoseException;
import service.worked.notification.Key;
import service.worked.notification.dto.Enpoint;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Path("subscription")
public class Subscription {

    @Inject
    Key key;

    public PushService pushService;

    private Map<String, nl.martijndwars.webpush.Subscription> subscriptions = new HashMap<>();

    @PostConstruct
    public void Subscription() throws IOException, GeneralSecurityException {
        Security.addProvider(new BouncyCastleProvider());
        pushService = new PushService(new String(key.getPublicKey()), new String(key.getPrivateKey()));
    }

    @POST
    public void subscribe(nl.martijndwars.webpush.Subscription subscription) {
        System.out.println("Nuevas subscripcion: " + subscription.endpoint);
        this.subscriptions.put(subscription.endpoint, subscription);
    }

    @POST
    @Path("unsubscribe")
    public void unsubscribe(Enpoint endpoint){
        System.out.println("Alguien se salio: " + endpoint.getValue());
        this.subscriptions.remove(endpoint.getValue());
    }

    @POST
    @Path("isSubscribe")
    public boolean isSubscribe(Enpoint endpoint){
        if(subscriptions.containsKey(endpoint.getValue())){
            System.out.println("Existe: " + endpoint.getValue());
            return true;
        }
        System.out.println("NO Existe: " + endpoint.getValue());
        return false;
    }

    public void sendNotification(nl.martijndwars.webpush.Subscription subscription, String messageJson){
        try {
            pushService.send(new Notification(subscription, messageJson));
        } catch (GeneralSecurityException | IOException | JoseException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Scheduled(every = "15s")
    public void numberFact() {
        if (!subscriptions.isEmpty()){
            for(Map.Entry<String, nl.martijndwars.webpush.Subscription> entry: subscriptions.entrySet()){
                System.out.println("Sending notifications to all subscribers");
                Map<String, Object> mensaje = new HashMap<>();
                mensaje.put("title", "Notificación");
                mensaje.put("body", "Esté es una notificación enviada desde el back");
                mensaje.put("timestamp", new Timestamp(System.currentTimeMillis()));
                mensaje.put("icon", "https://upload.wikimedia.org/wikipedia/commons/thumb/1/12/User_icon_2.svg/2048px-User_icon_2.svg.png");
                // mensaje.put("image", "https://upload.wikimedia.org/wikipedia/commons/thumb/f/fe/Video-Game-Controller-Icon-IDV-green.svg/2048px-Video-Game-Controller-Icon-IDV-green.svg.png");
                ObjectMapper objectMapper = new ObjectMapper();
                String response = "";
                try {
                    response = objectMapper.writeValueAsString(mensaje);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                sendNotification(entry.getValue(), response);
            }
        }
    }
}
