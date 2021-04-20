package communication;

import java.net.Socket;
import com.Packet;
import com.User;

public class Register extends Mediator {
    private String name;
    private int cid;
    private boolean success, waiting;
    private User user;

    public Register(Socket socket, String name, int cid) {
        super(socket);
        this.name = name;
        this.cid = cid;
        success = false;
        waiting = false;
    }

    @Override/**
     * Run the register exchange
     * Steps are:<br>
     *     1. HELLO
     *     2. dname|connectionid|RSA key
     *     2. RSA key exchange
     *     3. Successful connection/Waiting for peer
     * @return True if successful, false if not
     */
    public void run() {
        System.out.println("Hello from thread");

        // Run initial handshake
        if(!initialHandshake(name, cid)) {
            return;
        }

        success = keyExchange();
    }

    @Override
    public boolean getStatus() {
        return success;
    }

    public boolean waiting() {
        return waiting;
    }

    public User getUser() {
        return user;
    }

    /**
     * First step in registering. Handshake with server indicating intent to register.
     * Steps are: <br>
     *     1. HELLO
     *     2. username|connection id
     *     3. Success/error
     * @return Returns true if connected, false if error
     */
    private boolean initialHandshake(String name, int cid) {
        boolean sent;
        // Craft hello packet
        Packet hello = new Packet("REGISTER");
        hello.addPlaintext("CLIENT_HELLO");
        sent = send(hello);

        if(!sent) {
            return false;
        }

        Packet servHello = receive();

        // Check for valid hello
        if(servHello.getProtocol().equals("ERROR")) {
            System.out.println("Error from server:");
            System.out.println(servHello.getPlaintext());
            return false;
        }
        if(!servHello.getPlaintext(0).equals("SERVER_HELLO")) {
            System.out.println("Bad hello from server");
            return false;
        }

        /*
         * Send next packet to server
         * Format should be:
         *     Plaintext: username|connection id
         *     Encrypted (not really, but sent as bytes): RSA key
         */
        Packet registerPacket = new Packet("REGISTER");
        registerPacket.addPlaintext(name);
        registerPacket.addPlaintext(Integer.toString(cid));
        sent = send(registerPacket);
        System.out.println(sent);

        // Wait for successMessage from server
        Packet successMessage = receive();
        if(successMessage.getProtocol().equals("ERROR")) {
            System.out.println("Error registering:");
            return false;
        } else {
            System.out.println(successMessage.getPlaintext());
            user = new User(name, cid);
            user.setSocket(socket);
            return true;
        }
    }

    /**
     * Exchange keys with server. Get server's public key and send user's public key
     * @return Whether key exchange was successful
     */
    private boolean keyExchange() {
        return false;
    }
}
