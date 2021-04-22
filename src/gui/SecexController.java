package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import main.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
    private List<Message> messages;
    private Calendar cal;

    public SecexController() {
        messages = new ArrayList<Message>();  // List of all messages
        cal = Calendar.getInstance();  // Calender instance to get time
    }

    @FXML
    public void initialize() {
        // Add message to chat window
        chatWindow.appendText("[" + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + "] SERVER: Connected to <peer> ");

        // Set focus to textbox
        messageBox.requestFocus();
    }

    private void sendMessage() {
        // Get message user wants to send
        String message = messageBox.getText();

        // Clean message
        if(message.replaceAll("\\s", "").length() < 1) {
            // Set message box to nothing
            messageBox.setText("");
            return;
        }
        else {
            message = message.replaceAll("\\n", "");
        }

        // Check we've loaded user's name
        if(dname == null) {
            dname = loadName();
        }

        // Add message to list of messages
        String curTime = cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE);
        messages.add(new Message(dname, message, curTime));

        // Add message to message box
        chatWindow.appendText("\n" + "[" + curTime + "] " + dname + ": " + message);

        // Set message box to nothing
        messageBox.setText("");

        // Set focus to textbox
        messageBox.requestFocus();
    }

    public void buttonSend(ActionEvent actionEvent) {
        sendMessage();
    }

    public void quickSend(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.ENTER)) {
            sendMessage();
        }
    }

    public String loadName() {
        Stage stage = (Stage) ap.getScene().getWindow();

        User u = (User) stage.getUserData();
        return u.getName();
    }
}
