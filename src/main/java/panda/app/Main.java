package panda.app;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * JavaFX GUI for the Panda chatbot.
 */
public class Main extends Application {
    /**
        * Loads the main FXML view and displays the application window.
     *
     * @param stage the primary stage provided by JavaFX
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Main.class.getResource("/css/main.css").toExternalForm());
            stage.setTitle("Panda");
            stage.setMinWidth(420);
            stage.setMinHeight(520);
            stage.setScene(scene);
            stage.show();
        } catch (IOException | RuntimeException e) {
            throw new IllegalStateException("Unable to load Panda's GUI", e);
        }
    }
}
