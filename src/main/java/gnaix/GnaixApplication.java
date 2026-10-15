package gnaix;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Provides the JavaFX GUI for Gnaix.
 */
public class GnaixApplication extends Application {

    private Gnaix gnaix;

    /**
     * Starts the Gnaix GUI.
     *
     * @param stage Primary JavaFX stage.
     */
    @Override
    public void start(Stage stage) {
        try {
            gnaix = new Gnaix();

            FXMLLoader fxmlLoader =
                    new FXMLLoader(GnaixApplication.class.getResource("/view/MainWindow.fxml"));

            AnchorPane root = fxmlLoader.load();
            MainWindow mainWindow = fxmlLoader.getController();
            mainWindow.setGnaix(gnaix);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gnaix");
            stage.setMinWidth(450);
            stage.setMinHeight(600);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load Gnaix GUI.", e);
        }
    }
}
