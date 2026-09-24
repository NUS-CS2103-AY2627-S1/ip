package hampster.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** Provides the initial JavaFX window for Hampster. */
public class Main extends Application {

    /**
     * Starts the initial Hampster JavaFX interface.
     *
     * @param stage the primary JavaFX window
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    Main.class.getResource("/view/MainWindow.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            stage.setTitle("Hampster — Evil Task Lair");
            stage.setMinWidth(360.0);
            stage.setMinHeight(480.0);
            stage.setWidth(520.0);
            stage.setHeight(720.0);
            stage.setResizable(true);
            stage.setScene(scene);
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the main window", exception);
        }
    }
}
