package main;

import com.Packet;
import encryption.Secret;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.PublicKey;

public class User {
    private String name;
    final private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private int cid;
    private final Secret encryptor;
    private String secret;
    private PublicKey peerKey, peerSigningKey;

    public User(Socket socket) {
        this.socket = socket;
        encryptor = new Secret();

        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send message with a default of 5 max tries
     * @param packet The packet to send
     * @return Whether the packet was sent successfully
     */
    public boolean send(Packet packet) {
        return send(packet, 5);
    }

    /**
     * Send message the specified number of times
     * @param packet The packet to send
     * @param maxTries The maximum number of times to try sending the packet
     * @return Whether the packet was sent successfully
     */
    public boolean send(Packet packet, int maxTries) {
        boolean sent = false;
        int count = 0;
        System.out.println("Sending message: " + packet.getData());
        while(!sent && count < maxTries) {
            try {
                out.flush();
                out.writeObject(packet);
                sent = true;
            } catch(IOException e) {
                System.out.println("Sending failed, retrying...");
                count++;
            }
        }

        if(sent) {
            System.out.println("Message sent");
        } else {
            System.out.println("Error sending message");
        }
        return sent;
    }

    public Packet receive() {
        Packet packet = null;
        try {
            packet = (Packet) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return packet;
    }

    public void endConnection() {
        boolean connected = true;
        while(connected) {
            try {
                socket.close();
                connected = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Ended connection");
    }

    public Secret getEncryptor() {
        return encryptor;
    }

    public SecretKey generateAES() {
        return encryptor.generateAESKey();
    }

    public void setAESKey(SecretKey key) {
        encryptor.setAESKey(key);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getCid() {
        return cid;
    }

    public PublicKey getPublicKey() {
        return encryptor.getPublicKey();
    }

    public void setPeerKey(PublicKey key) {
        peerKey = key;
    }

    public PublicKey getPeerKey() {
        return peerKey;
    }

    public void setPeerSigningKey(PublicKey signingKey) {
        peerSigningKey = signingKey;
    }

    public PublicKey getPeerSigningKey() {
        return peerSigningKey;
    }

    // TODO - Get and set symmetric key
    public void setSymmetricKey() {

    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
