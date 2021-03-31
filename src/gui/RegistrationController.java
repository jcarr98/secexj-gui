package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import user.User;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;

public class RegistrationController {
    @FXML private TextField dname;
    @FXML private TextField cid;

    public void submit(ActionEvent actionEvent) {
        System.out.println("Submitting...");

        // Check that display name and cid are valid
        String name = dname.getText();
        String id = cid.getText();
        System.out.println("Display name: " + name);
        System.out.println("Connection ID: " + id);

        // Switch scenes
        sendData(actionEvent, name, id);
    }

    private void sendData(ActionEvent actionEvent, String name, String id) {
        // Create user
        User user = new User(name, id);

        // Get node
        Node node = (Node) actionEvent.getSource();

        // Close stage
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();

        try {
            // Load scene
            Parent root = FXMLLoader.load(getClass().getResource("secex.fxml"));

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

        System.out.println("Sent " + user.getName());
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
