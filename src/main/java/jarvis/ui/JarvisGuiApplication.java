package jarvis.ui;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Main application class for launching the Jarvis GUI.
 */
public class JarvisGuiApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file - use the correct filename
        URL fxmlLocation = getClass().getResource("/view/JarvisGui.fxml");

        if (fxmlLocation == null) {
            throw new RuntimeException("FXML file not found: /view/JarvisGui.fxml");
        }


        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        AnchorPane root = loader.load();

        // Create the scene
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("J.A.R.V.I.S. - Personal Task Management System");
        primaryStage.setMinWidth(670);
        primaryStage.setMinHeight(655);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
