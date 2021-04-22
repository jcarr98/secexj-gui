package gui;

import setup.Register;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import main.User;

import java.io.IOException;
import java.net.Socket;

public class RegistrationController {
    @FXML private TextFlow infoBox;
    @FXML private TextField dname;
    @FXML private TextField cid;

    public RegistrationController() {}

    @FXML
    public void initialize() {
        infoBox.getChildren().add(new Text(""));
    }

    public void submit(ActionEvent actionEvent) {
        // Check that display name and cid are valid
        String name = dname.getText();
        String id = cid.getText();
        if(name.replaceAll("\\s+", "").length() == 0) {
            info("Name cannot be blank", true);
            return;
        }
        else if(id.replaceAll("\\s+", "").length() == 0) {
            info("Connection ID cannot be blank", true);
            return;
        }

        int intId;
        try {
            intId = Integer.parseInt(id);
        } catch(NumberFormatException e) {
            info("Connection ID can only be a whole number", true);
            return;
        }

        if(intId < 0) {
            info("Connection ID can only be positive", true);
            return;
        }

        dname.setDisable(true);
        cid.setDisable(true);
        info("Connecting to server...");

        // Connect to server
        Socket socket = connect();

        if(socket == null) {
            info("Error connecting to server", true);
            dname.setDisable(false);
            cid.setDisable(false);
            return;
        }
        info("Registering...");

        // Create user and register them with server
        User user = new User(socket);
        user.setName(name);
        user.setCid(intId);
        Register reg = new Register(user);
        boolean registered = reg.run();
        if(registered) {
            info("Registration successful! Loading app...");
            // Get updated user
            user = reg.getUser();
        } else {
            info("Registration unsuccessful, check logs", true);
            dname.setDisable(false);
            cid.setDisable(false);
            return;
        }

        // Switch scenes
        // Get node
        Node node = (Node) actionEvent.getSource();
        // Get stage
        Stage stage = (Stage) node.getScene().getWindow();
        sendData(stage, user);
    }

    private void info(String message) {
        info(message, false);
    }

    private void info(String message, boolean error) {
        clearInfo();

        Text messageText = new Text(message + "\n");
        if(error) {
            dname.setText("");
            cid.setText("");
            messageText.setFill(Color.RED);
        }

        infoBox.getChildren().set(0, messageText);
    }

    private void clearInfo() {
        infoBox.getChildren().set(0, new Text(""));
    }

    private Socket connect() {
        Socket socket;
        try {
            socket = new Socket("127.0.0.1", 8008);
        } catch (IOException e) {
            return null;
        }

        return socket;
    }

    private void sendData(Stage stage, User user) {
        // Close stage
        stage.close();

        try {
            // Load scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("waiting.fxml"));
            Parent root = (Parent)loader.load();

            if(root == null) {
                System.out.println("Error loading next scene");
                System.exit(1);
                return;
            }

            // Pass information
            stage.setUserData(user);
            WaitingController controller = (WaitingController) loader.getController();
            controller.loadUser(stage);
            controller.runWait();

            // Create new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);

            // Show stage
            stage.show();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}
