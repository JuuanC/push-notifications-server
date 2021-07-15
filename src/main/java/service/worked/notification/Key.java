package service.worked.notification;

import javax.enterprise.context.ApplicationScoped;
import java.io.*;

@ApplicationScoped
public class Key {
    public byte[] getPublicKey() throws IOException {
        File file = new File("../src/main/resources/key_public.txt");

        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
        byte[] arrayBytes = inputStream.readAllBytes();
        return arrayBytes;
    }

    public byte[] getPrivateKey() throws IOException {
        File file = new File("../src/main/resources/key_private.txt");

        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
        byte[] arrayBytes = inputStream.readAllBytes();
        return arrayBytes;
    }
}
