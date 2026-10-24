package shannon.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import shannon.Shannon;

/**
 * The JavaFX application: builds the window from FXML and puts a chatbot behind it.
 * <p>
 * The work is deliberately thin. Everything about how the window looks is in
 * {@code /view/MainWindow.fxml}, and everything about how it behaves is in {@link MainWindow};
 * this class only loads the one, hands the chatbot to the other, and shows the result.
 */
public class Main extends Application {

    /**
     * The smallest height, in pixels, the window can be shrunk to.
     * Small enough to tuck the window into a corner of the screen beside other work, but still
     * tall enough to show a few exchanges above the input bar.
     */
    private static final double WINDOW_MIN_HEIGHT = 320.0;

    /** The smallest width, in pixels, the window can be shrunk to, before text wraps too often. */
    private static final double WINDOW_MIN_WIDTH = 300.0;

    /** The chatbot the window talks to. The same class the terminal version uses. */
    private final Shannon shannon = new Shannon();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            VBox root = fxmlLoader.load();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle("Shannon");
            // A minimum only, with no maximum: the window is freely resizable, and the layout
            // in the FXML redistributes the space rather than leaving the extra room blank.
            stage.setMinHeight(WINDOW_MIN_HEIGHT);
            stage.setMinWidth(WINDOW_MIN_WIDTH);

            // Only available after load(), which is what creates the controller.
            fxmlLoader.<MainWindow>getController().setShannon(shannon);

            stage.show();
        } catch (IOException e) {
            // The FXML is packaged with the program, so a failure here means a broken build
            // rather than anything the user did.
            e.printStackTrace();
            // No window was shown, so there is nothing for the user to close, and JavaFX would
            // keep running invisibly. Shut it down instead.
            Platform.exit();
        }
    }
}
