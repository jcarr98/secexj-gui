package communication;

import java.io.IOException;
import com.Packet;
import main.User;

abstract public class Mediator {
    final protected User user;

    public Mediator(User user) {
        this.user = user;
    }

    abstract public void run();

    abstract public boolean getStatus();
}
