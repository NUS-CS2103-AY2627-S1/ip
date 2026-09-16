package zucc;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import zucc.ui.MainWindow;

/**
 * Configures and displays the JavaFX interface for Zucc.
 */
public final class Main extends Application {
    /** Initial width of the application window. */
    private static final double WINDOW_INITIAL_WIDTH = 720.0;

    /** Initial height of the application window. */
    private static final double WINDOW_INITIAL_HEIGHT = 680.0;

    /** Minimum width at which the application layout remains usable. */
    private static final double WINDOW_MINIMUM_WIDTH = 420.0;

    /** Minimum height at which the application layout remains usable. */
    private static final double WINDOW_MINIMUM_HEIGHT = 480.0;

    /**
     * Loads the main view, connects it to Zucc, and displays the primary stage.
     *
     * @param stage primary JavaFX application window.
     * @throws IOException if the main FXML view cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        URL viewUrl = Objects.requireNonNull(
                Main.class.getResource("/view/MainWindow.fxml"),
                "MainWindow.fxml is missing");
        FXMLLoader fxmlLoader = new FXMLLoader(viewUrl);
        Parent root = fxmlLoader.load();
        MainWindow mainWindow = fxmlLoader.getController();
        // The bundled view must declare the controller that connects it to the application.
        assert mainWindow != null : "MainWindow.fxml must declare its controller";

        Scene scene = new Scene(root, WINDOW_INITIAL_WIDTH, WINDOW_INITIAL_HEIGHT);
        URL styleUrl = Objects.requireNonNull(
                Main.class.getResource("/styles/main.css"),
                "main.css is missing");
        scene.getStylesheets().add(styleUrl.toExternalForm());

        stage.setTitle("Zucc");
        stage.setMinWidth(WINDOW_MINIMUM_WIDTH);
        stage.setMinHeight(WINDOW_MINIMUM_HEIGHT);
        stage.setScene(scene);

        try {
            mainWindow.setZucc(new Zucc());
        } catch (ZuccException exception) {
            mainWindow.showUnavailable(exception.getMessage());
        }

        stage.show();
        mainWindow.focusInput();
    }
}
