package setup;

import java.net.Socket;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import com.Packet;
import main.User;

public class Register {
    private boolean success, waiting;
    private User user;

    public Register(User user) {
        this.user = user;
        success = false;
        waiting = false;
    }

    /**
     * Runs the register exchange
     * Steps are:<br>
     *     1. HELLO|connection id
     *     2. dname|RSA key
     *     3. Successful connection/Waiting for peer
     * @return True if successful, false if not
     */
    public boolean run() {
        boolean sent;
        // Craft hello packet
        Packet hello = new Packet("REGISTER");
        hello.addData("CLIENT_HELLO");
        hello.addData(Integer.toString(user.getCid()));
        sent = user.send(hello);

        if(!sent) {
            return false;
        }

        System.out.println("Waiting for server's hello...");
        Packet servHello = user.receive();

        // Check for valid hello
        if(servHello.getProtocol().equals("ERROR")) {
            System.out.println("Error from server:");
            System.out.println(servHello.getData());
            return false;
        }
        if(!servHello.getData(0).equals("SERVER_HELLO")) {
            System.out.println("Bad hello from server");
            return false;
        }

        /*
         * Send next packet to server
         * Format should be:
         *     Plaintext: username|RSA key
         *     Encrypted (not really, but sent as bytes): RSA key
         */
        // Get RSA key
        PublicKey uKey = user.getPublicKey();
        // Convert key to bytes
        byte[] bKey = uKey.getEncoded();
        // Base64 encode
        String key = Base64.getEncoder().encodeToString(bKey);

        // Build packet
        Packet registerPacket = new Packet("REGISTER");
        registerPacket.addData(user.getName());
        registerPacket.addData(key);
        sent = user.send(registerPacket);

        if(!sent) {
            System.out.println("Failed to send register packet");
            return false;
        }

        // Wait for successMessage from server
        // Success message should contain server's key: SUCCESS
        Packet successMessage = user.receive();
        if(successMessage.getProtocol().equals("ERROR")) {
            System.out.println("Error registering:");
            return false;
        } else {
            return true;
        }
    }

    public boolean waiting() {
        return waiting;
    }

    public User getUser() {
        return user;
    }
}
