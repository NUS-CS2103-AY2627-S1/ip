package davidgoggins;

import java.io.IOException;

import davidgoggins.gui.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * The JavaFX application that shows the chatbot in a window.
 *
 * <p>Builds the window from {@code MainWindow.fxml} and hands the controller the
 * chatbot to talk to, so that the layout stays in the FXML and the wiring stays here.
 */
public class Main extends Application {

    /** The text shown in the window's title bar. */
    private static final String WINDOW_TITLE = "David Goggins";

    /** The smallest height, in pixels, the user can shrink the window to. */
    private static final double MIN_WINDOW_HEIGHT = 360.0;

    /**
     * The smallest width, in pixels, the user can shrink the window to.
     *
     * <p>Narrow enough to tuck the app beside other windows; the bubbles re-wrap to fit.
     */
    private static final double MIN_WINDOW_WIDTH = 340.0;

    /** The chatbot the window sends the user's commands to. */
    private final DavidGoggins davidGoggins = new DavidGoggins();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            Scene scene = new Scene(root);
            // Added to the scene rather than to each FXML file, so every dialog box
            // added later picks the styling up automatically.
            scene.getStylesheets().add(Main.class.getResource("/css/main.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle(WINDOW_TITLE);
            stage.setMinHeight(MIN_WINDOW_HEIGHT);
            stage.setMinWidth(MIN_WINDOW_WIDTH);

            fxmlLoader.<MainWindow>getController().setDavidGoggins(davidGoggins);
            stage.show();
        } catch (IOException e) {
            // Only thrown if the FXML is missing or malformed, which is a packaging
            // fault rather than anything the user can act on.
            e.printStackTrace();
        }
    }
}
