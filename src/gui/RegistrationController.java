package gui;

import communication.Mediator;
import communication.Register;
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
import com.User;

import java.io.IOException;
import java.net.Socket;

public class RegistrationController {
    @FXML private TextFlow infoBox;
    @FXML private TextField dname;
    @FXML private TextField cid;
    private User user;

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
        Socket socket = null;
        try {
            socket = new Socket("127.0.0.1", 8008);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(socket == null) {
            info("Error connecting to server", true);
            dname.setDisable(false);
            cid.setDisable(false);
            return;
        }
        info("Registering...");

        // Create registration object and run
        Register reg = new Register(socket, name, intId);
        reg.run();
        boolean registered = reg.getStatus();
        if(registered) {
            info("Registration successful! Loading app...");
        } else {
            info("Registration unsuccessful, check logs", true);
            dname.setDisable(false);
            cid.setDisable(false);
        }

        // Create user
        user = reg.getUser();

        // Switch scenes
        //sendData(actionEvent, name, cid, reg.waiting());
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

    private void sendData(ActionEvent actionEvent, String name, int id, boolean waiting) {
        // Create user
        User user = new User(name, id);

        // Get node
        Node node = (Node) actionEvent.getSource();

        // Close stage
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();

        try {
            // Load scene
            Parent root;
            if(waiting) {
                root = FXMLLoader.load(getClass().getResource("waiting.fxml"));
            } else {
                root = FXMLLoader.load(getClass().getResource("secex.fxml"));
            }

            if(root == null) {
                System.out.println("Error loading next scene");
                System.exit(1);
                return;
            }

            // Pass information
            stage.setUserData(user);

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

    private void switchScene(User user) {
        // Get the stage
        Stage st = (Stage) dname.getScene().getWindow();

        // Load the scene
        Parent scene;
        try {
            scene = FXMLLoader.load(getClass().getResource("secex.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Change scene to application
        st.setTitle("Secure Exchange");
        st.setScene(new Scene(scene));
    }
}
