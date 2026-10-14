// Adapted from the SE-EDU JavaFX Tutorial, Part 4:
// https://se-education.org/guides/tutorials/javaFxPart4.html

package bingus;

import java.io.IOException;

import bingus.ui.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Represents the Bingus JavaFX application using FXML.
 */
public class Main extends Application {
    private static final String APPLICATION_TITLE = "Bingus";
    private static final double MINIMUM_WINDOW_HEIGHT = 440;
    private static final double MINIMUM_WINDOW_WIDTH = 340;

    private final Bingus bingus = new Bingus(Bingus.DEFAULT_SAVE_FILE_PATH);

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            Parent rootPane = fxmlLoader.load();
            Scene scene = new Scene(rootPane);

            stage.setScene(scene);
            stage.setTitle(APPLICATION_TITLE);
            stage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/DaBingus.png")));
            stage.sizeToScene();
            stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
            stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
            stage.setResizable(true);
            fxmlLoader.<MainWindow>getController().setBingus(bingus);
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load the main window layout.", e);
        }
    }
}
