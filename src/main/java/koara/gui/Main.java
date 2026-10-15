package koara.gui;

import java.io.IOException;
import java.util.Objects;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import koara.Koara;

/**
 * Displays Koara's graphical user interface.
 */
public class Main extends Application {
    private static final int MINIMUM_WINDOW_HEIGHT = 400;
    private static final int MINIMUM_WINDOW_WIDTH = 360;
    private static final int PREFERRED_WINDOW_HEIGHT = 620;
    private static final int PREFERRED_WINDOW_WIDTH = 460;

    private final Koara koara = new Koara();

    /**
     * Loads and displays Koara's main window.
     *
     * @param stage Primary stage supplied by JavaFX.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            fxmlLoader.<MainWindow>getController().setKoara(koara);

            Scene scene = new Scene(root, PREFERRED_WINDOW_WIDTH, PREFERRED_WINDOW_HEIGHT);
            scene.getStylesheets().add(Objects.requireNonNull(
                    Main.class.getResource("/css/main.css"),
                    "Missing GUI stylesheet").toExternalForm());
            stage.setScene(scene);
            stage.getIcons().add(new Image(Objects.requireNonNull(
                    Main.class.getResourceAsStream("/images/DaKoara.png"),
                    "Missing Koara app icon")));
            stage.setTitle("KOARA — Steady Mode");
            stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
            stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
            stage.show();
        } catch (IOException exception) {
            throw new RuntimeException("Unable to load Koara's main window.", exception);
        }
    }
}
