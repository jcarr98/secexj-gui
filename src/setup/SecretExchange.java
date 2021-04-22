package setup;

import com.Packet;
import encryption.Secret;
import main.User;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Random;

public class SecretExchange implements Runnable {
    final private User user;
    final private Secret encryptor;
    String peerName;
    boolean success;

    public SecretExchange(User user) {
        this.user = user;
        encryptor = user.getEncryptor();
        success = false;
    }

    @Override
    public void run() {
        System.out.println("Waiting for peer as " + user.getName() + " on server " + user.getCid());
        /*
         * Wait for first packet from server. It should have the protocol SECRET
         * There will be four fields sent:
         *      Peer name|Peer RSA key|Peer signing key|secret message
         * The secret message can be one of two messages:
         *      SECRETKEY
         *      SECRETCODE
         * If the user receives SECRETKEY, they should generate a secret AES key to be used during communications
         * If the user receives SECRETCODE, they should generate 32 random characters to be included with each message
         *   to help prove identity and integrity
        */

        Packet packet = user.receive();
        // Check for proper protocol
        if(!packet.getProtocol().equals("SECRET")) {
            System.out.println("Improper protocol");
            return;
        }

        // Load in data
        String peerRSAKey;
        String peerRSASigningKey;
        String message;
        try {
            peerName = packet.getData(0);
            peerRSAKey = packet.getData(1);
            peerRSASigningKey = packet.getData(2);
            message = packet.getData(3);
        } catch(IndexOutOfBoundsException e) {
            System.out.println("Improper data sent");
            return;
        }

        // Convert peer's keys to usable forms
        boolean keysSaved = savePeerInfo(peerRSAKey, peerRSASigningKey);
        if(!keysSaved) {
            System.out.println("Error saving peer's keys");
            return;
        }

        // Check message for job
        if(message.equals("SECRETKEY")) {
            success = secretKey();
        }
        else if(message.equals("SECRETCODE")) {
            success = secretCode();
        }
        else {
            System.out.println("Unknown job");
        }
    }

    private boolean savePeerInfo(String peerRSAKey, String peerRSASigningKey) {
        // Convert peer's keys to usable forms
        byte[] bKey = Base64.getDecoder().decode(peerRSAKey);
        byte[] bSigningKey = Base64.getDecoder().decode(peerRSASigningKey);
        PublicKey pKey, pSigningKey;
        try {
            pKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(bKey));
            pSigningKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(bSigningKey));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }

        user.setPeerKey(pKey);
        user.setPeerSigningKey(pSigningKey);

        return true;
    }

    private boolean secretKey() {
        // Generate secret key
        SecretKey key = encryptor.generateAESKey();
        user.setAESKey(key);

        // Convert secret key to string
        String sKey = Base64.getEncoder().encodeToString(key.getEncoded());

        // Sign key
        String keySignature = encryptor.sign(sKey);

        // Encrypt secret key
        String eKey = encryptor.encryptRSA(sKey, user.getPeerKey());

        /* Create packet
         * Packet should have structure:
         *     KEY|AES key|Signature
        */
        Packet packet = new Packet("SECRET");
        packet.addData("KEY");
        packet.addData(eKey);
        packet.addData(keySignature);

        return user.send(packet);
    }

    private boolean secretCode() {
        byte[] chars = new byte[32];
        new Random().nextBytes(chars);
        String randomChars = new String(chars, StandardCharsets.UTF_8);

        // Get signature on chars
        String charSig = encryptor.sign(randomChars);

        // Encrypt random chars
        String eChars = encryptor.encryptRSA(randomChars, user.getPeerKey());

        /* Create packet
         * Packet should have structure:
         *     CODE|code|signature
        */
        Packet packet = new Packet("SECRET");
        packet.addData("CODE");
        packet.addData(eChars);
        packet.addData(charSig);

        boolean sent = user.send(packet);

        return sent;
    }

    public boolean getSuccess() {
        return success;
    }
}
