package nova;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for Nova built with JavaFX and FXML. The theme and the window position
 * the user last chose are restored on the next run.
 */
public class Main extends Application {
    private static final double MIN_WINDOW_WIDTH = 380;
    private static final double MIN_WINDOW_HEIGHT = 460;

    private final Nova nova = new Nova();
    private final Settings settings = new Settings();

    /**
     * Shows the main chat window.
     *
     * @param stage the primary stage provided by JavaFX
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane mainWindow = fxmlLoader.load();
            Scene scene = new Scene(mainWindow, settings.getWindowWidth(), settings.getWindowHeight());
            scene.getStylesheets().add(Main.class.getResource("/view/nova.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Nova");
            stage.setMinWidth(MIN_WINDOW_WIDTH);
            stage.setMinHeight(MIN_WINDOW_HEIGHT);
            restoreWindowPosition(stage);
            MainWindow controller = fxmlLoader.getController();
            controller.setNova(nova);
            controller.setSettings(settings, mainWindow);
            scene.getAccelerators().put(
                    new KeyCodeCombination(KeyCode.D, KeyCombination.SHORTCUT_DOWN), controller::toggleTheme);
            stage.setOnHidden(event -> settings.saveWindowBounds(stage.getX(), stage.getY(),
                    stage.getWidth(), stage.getHeight()));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Puts the window back where it was, unless it was never moved or closed. */
    private void restoreWindowPosition(Stage stage) {
        if (settings.getWindowX() >= 0 && settings.getWindowY() >= 0) {
            stage.setX(settings.getWindowX());
            stage.setY(settings.getWindowY());
        }
    }
}
