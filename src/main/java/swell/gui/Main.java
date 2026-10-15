package swell.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import swell.Swell;

/**
 * Provides the JavaFX GUI for Swell.
 */
public class Main extends Application {
    private static final String MAIN_WINDOW_FXML = "/view/MainWindow.fxml";
    private static final String APP_TITLE = "Swell";
    private static final double MIN_WINDOW_HEIGHT = 360.0;
    private static final double MIN_WINDOW_WIDTH = 420.0;

    private final Swell swell = new Swell();

    /**
     * Starts the Swell GUI.
     *
     * @param stage main application window.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(MAIN_WINDOW_FXML));
            AnchorPane root = fxmlLoader.load();
            Scene scene = new Scene(root);

            stage.setTitle(APP_TITLE);
            stage.setMinHeight(MIN_WINDOW_HEIGHT);
            stage.setMinWidth(MIN_WINDOW_WIDTH);
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setSwell(swell);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
