package pan;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * The JavaFX application: loads {@code MainWindow.fxml}, hands the
 * {@link MainWindow} controller a {@link Pan} back end, and shows the window.
 *
 * <p>Since Part 4 the layout lives in the FXML files under
 * {@code src/main/resources/view/}, not in Java code.
 */
public class Main extends Application {

    /** Minimum window width, in pixels. */
    private static final double MIN_WIDTH = 400.0;

    /** Minimum window height, in pixels. */
    private static final double MIN_HEIGHT = 600.0;

    private final Pan pan = new Pan();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            fxmlLoader.<MainWindow>getController().setPan(pan);

            stage.setScene(new Scene(root));
            stage.setTitle("PanPan");
            stage.setMinWidth(MIN_WIDTH);
            stage.setMinHeight(MIN_HEIGHT);
            stage.show();
        } catch (IOException e) {
            // The main window failing to load is fatal - there is nothing to
            // fall back to, so surface it instead of swallowing it.
            throw new RuntimeException(e);
        }
    }
}