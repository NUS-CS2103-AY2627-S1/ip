package yawned;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import yawned.gui.MainWindow;

/**
 * Starts the Yawned JavaFX application.
 */
public class Main extends Application {
    private static final double MINIMUM_WINDOW_HEIGHT = 220;
    private static final double MINIMUM_WINDOW_WIDTH = 417;

    private final Yawned yawned = new Yawned(ApplicationPaths.taskFile());

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();
            MainWindow mainWindow = fxmlLoader.getController();
            mainWindow.setYawned(yawned);

            stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
            stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
            stage.setResizable(true);

            stage.setScene(new Scene(anchorPane));
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the main window layout.", exception);
        }
    }
}
