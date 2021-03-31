import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    // Make primary stage a class variable
    private Stage pStage;
    @Override
    public void start(Stage primaryStage) throws Exception{
        pStage = primaryStage;

        // Load registration window
        Parent root = FXMLLoader.load(getClass().getResource("gui/register.fxml"));
        pStage.setTitle("Registration");
        pStage.setScene(new Scene(root));
        pStage.centerOnScreen();
        pStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
