package aster.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Starts the FXML-based Aster user interface.
 */
public class Main extends Application {
    /**
     * Loads and displays the main window.
     *
     * @param stage primary JavaFX stage.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = loader.load();
            stage.setScene(new Scene(root));
            stage.setTitle("Aster");
            stage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/aster-bot.png")));
            stage.setMinHeight(220);
            stage.setMinWidth(417);
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the Aster GUI.", exception);
        }
    }
}
