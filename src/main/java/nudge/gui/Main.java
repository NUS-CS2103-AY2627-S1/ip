package nudge.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import nudge.Nudge;

/**
 * Displays the Nudge graphical interface using its FXML view.
 */
public class Main extends Application {
    private static final double MINIMUM_WINDOW_HEIGHT = 420;
    private static final double MINIMUM_WINDOW_WIDTH = 420;

    private final Nudge nudge = new Nudge();

    /**
     * Loads and displays the main Nudge window.
     *
     * @param stage primary JavaFX window.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane mainWindow = fxmlLoader.load();
            Scene scene = new Scene(mainWindow);

            stage.setTitle("Nudge - Your friendly task assistant");
            stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
            stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setNudge(nudge);
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the Nudge interface.", exception);
        }
    }
}
