package mochi;

import java.io.IOException;
import java.nio.file.Path;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import mochi.ui.MainWindow;

/**
 * Starts Mochi's JavaFX user interface.
 */
public class Main extends Application {
    private final Mochi mochi = new Mochi(Path.of("data", "duke.txt"));

    /**
     * Creates the JavaFX application instance.
     */
    public Main() {
    }

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane mainWindow = fxmlLoader.load();
            Scene scene = new Scene(mainWindow);
            stage.setTitle("Mochi");
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setMochi(mochi);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
