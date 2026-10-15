package kdb;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Provides the JavaFX graphical user interface for Kdb.
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader =
                    new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));

            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(
                    Main.class.getResource("/style.css").toExternalForm());

            stage.setTitle("Kdb");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Could not load MainWindow.fxml", e);
        }
    }
}
