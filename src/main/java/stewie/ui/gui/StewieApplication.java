package stewie.ui.gui;

import java.net.URL;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import stewie.model.TaskList;

/**
 * Represents the entry point for Stewie's JavaFX user interface.
 */
public class StewieApplication extends Application {

    /**
     * Creates and displays the Stewie application window.
     *
     * @param stage The primary application window.
     */
    @Override
    public void start(Stage stage) {
        StewieGui gui = new StewieGui(new TaskList());
        Scene scene = new Scene(gui, 1180, 760);
        URL stylesheet = getClass().getResource("/stewie/ui/gui/instagram.css");
        if (stylesheet != null) {
            scene.getStylesheets().add(stylesheet.toExternalForm());
        }

        stage.setTitle("Stewie — your task studio");
        stage.setMinWidth(960);
        stage.setMinHeight(640);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Launches the JavaFX application.
     *
     * @param args Command-line arguments supplied to the application.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
