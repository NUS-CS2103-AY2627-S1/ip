package amadeus;

import java.io.IOException;
import java.net.URL;

import amadeus.ui.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The JavaFX application: builds the window and puts a chatbot behind it.
 * <p>
 * The window's appearance is described in {@code MainWindow.fxml} rather than in code,
 * so this class only has to load that file, hand the resulting controller an
 * {@link Amadeus} to talk to, and show the stage.
 */
public class Main extends Application {

    /** Smallest height the window can be resized to, in pixels. */
    private static final double MIN_WINDOW_HEIGHT = 220;

    /**
     * Smallest width the window can be resized to, in pixels.
     * Narrow enough to sit beside other windows, but not so narrow that a task line
     * wraps after two or three words.
     */
    private static final double MIN_WINDOW_WIDTH = 320;

    private final Amadeus amadeus = new Amadeus();

    /**
     * Builds and shows the window.
     *
     * @param stage the window JavaFX has created for us to fill.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root);
            applyStylesheet(scene);
            stage.setScene(scene);
            stage.setTitle("Amadeus");
            stage.setMinHeight(MIN_WINDOW_HEIGHT);
            stage.setMinWidth(MIN_WINDOW_WIDTH);

            // The controller only exists once the FXML has been loaded, which is why the
            // chatbot is injected here rather than passed to a constructor.
            fxmlLoader.<MainWindow>getController().setAmadeus(amadeus);
            stage.show();
        } catch (IOException e) {
            // The FXML is packaged inside the JAR, so failing to read it means the build
            // is broken rather than anything the user can act on. There is no window to
            // report it in, so the console is the only place left.
            System.err.println("Could not load the main window: " + e.getMessage());
        }
    }

    /**
     * Applies the app's stylesheet to the given scene, or leaves it with JavaFX's own
     * default look if the file is missing.
     * <p>
     * A missing stylesheet is not worth stopping the app for, the same way a missing
     * image would not be: every control still works, it just looks plain. Passing the
     * URL without checking would throw a {@link NullPointerException} instead, which
     * would take the whole window down over an appearance problem.
     */
    private static void applyStylesheet(Scene scene) {
        URL stylesheet = Main.class.getResource("/view/amadeus.css");
        if (stylesheet == null) {
            System.err.println("Could not find the stylesheet; using default styling.");
            return;
        }
        scene.getStylesheets().add(stylesheet.toExternalForm());
    }
}
