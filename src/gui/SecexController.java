package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import user.User;

import java.util.Scanner;

public class SecexController {
    // FXML items
    @FXML private AnchorPane ap;
    @FXML private TextArea messageBox;
    @FXML private TextArea chatWindow;
    @FXML private Button sendButton;
    @FXML private Button uploadButton;
    @FXML private Button downloadButton;
    @FXML private Label peerNameLabel;
    @FXML private ListView downloadBox;

    // Class items
    private String dname;
    private Stage stage;

    public void initialize() {
        //receiveData();
        stage = (Stage) ap.getScene().getWindow();
        User u = (User) stage.getUserData();
        dname = u.getName();
    }

    public void sendMessage(ActionEvent actionEvent) {
        // Get message user wants to send
        System.out.print("Sending message: ");
        System.out.println(messageBox.getText());

        // Set message box to nothing
        messageBox.setText("");

        // Add message to
    }

    public void checkName(ActionEvent actionEvent) {
//        Node node = (Node) actionEvent.getSource();
//        Stage stage = (Stage) node.getScene().getWindow();
//
//        User u = (User) stage.getUserData();
//
//        System.out.println(u.getName());

        System.out.println(dname);
    }

    private void loadData() {
        System.out.println("loading data...");
        Scanner reader = new Scanner("userdata.txt");

        while(reader.hasNextLine()) {
            String data = reader.nextLine();
            String[] line = data.split("=", 2);
            if(line[0].equals("dname")) {
                dname = line[1];
            }
        }
    }
}
