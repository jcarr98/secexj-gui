package communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import com.Packet;

abstract public class Mediator {
    Socket socket;
    ObjectInputStream in;
    ObjectOutputStream out;

    public Mediator(Socket socket) {
        this.socket = socket;

        try {
            in = new ObjectInputStream(this.socket.getInputStream());
            out = new ObjectOutputStream(this.socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    abstract public void run();

    abstract public boolean getStatus();

    protected boolean send(Packet packet) {
        return send(packet, 5);
    }

    protected boolean send(Packet packet, int maxTries) {
        System.out.println("Sending packet");
        boolean sent = false;
        int count = 0;
        while(!sent && count < maxTries) {
            try {
                out.writeObject(packet);
                sent = true;
            } catch(IOException e) {
                System.out.println("Sending failed, retrying...");
                count++;
            }
        }

        return sent;
    }

    protected Packet receive() {
        Packet packet;
        try {
            packet = (Packet) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            packet = null;
        }

        return packet;
    }
}
