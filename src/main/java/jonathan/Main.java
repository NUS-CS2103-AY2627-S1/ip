package jonathan;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Starts the JavaFX GUI for Griddel.
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Main.class.getResource("/view/MainWindow.fxml"));

            AnchorPane mainWindow = loader.load();
            Scene scene = new Scene(mainWindow);
            String stylesheet = Main.class.getResource(
                    "/view/Styles.css").toExternalForm();
            scene.getStylesheets().add(stylesheet);

            stage.setTitle("Griddel");
            stage.setMinWidth(360);
            stage.setMinHeight(400);
            stage.setResizable(true);
            stage.setScene(scene);
            stage.show();
        } catch (IOException exception) {
            throw new RuntimeException(
                    "Unable to load MainWindow.fxml", exception);
        }
    }
}
