package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import main.User;
import setup.SecretExchange;

public class WaitingController {
    @FXML private AnchorPane ap;

    private User user;

    @FXML
    public void initialize() {

    }

    public void runWait() {
        SecretExchange exchange = new SecretExchange(user);
        Thread t = new Thread(exchange);
        t.setDaemon(true);
        t.start();
    }

    public void loadUser(Stage stage) {
        user = (User) stage.getUserData();
    }
}
