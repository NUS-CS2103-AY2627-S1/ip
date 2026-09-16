package lebronjames.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lebronjames.LebronJames;

/**
 * The JavaFX application itself: it builds the one window the chatbot uses.
 *
 * <p>The window's contents are described in {@code /view/MainWindow.fxml} rather
 * than built by hand here, so the layout can be changed without touching Java
 * code. This class only loads that file, hands the controller a chatbot to talk
 * to, and shows the result.
 *
 * <p>It is started by {@link lebronjames.Launcher} rather than having a
 * {@code main} of its own; see that class for why.
 */
public class Main extends Application {
    /**
     * Size the window opens at.
     *
     * <p>It is given to the Scene rather than left to the FXML. A window sized
     * from its own contents would open as wide as the longest line the chatbot
     * ever says, which for the start-up message is most of the screen; stating
     * the size here means the conversation wraps to the window instead of the
     * window stretching to the conversation.
     */
    private static final double INITIAL_WIDTH = 460.0;
    private static final double INITIAL_HEIGHT = 620.0;

    /**
     * Smallest window that still shows a readable conversation.
     *
     * <p>The window is otherwise free to be resized to anything larger; the
     * layout in the FXML gives the extra space to the conversation. A minimum
     * is still worth setting, because below roughly this size the task list
     * lines start wrapping and stop lining up.
     */
    private static final double MINIMUM_WIDTH = 360.0;
    private static final double MINIMUM_HEIGHT = 320.0;

    private final LebronJames lebronJames =
            new LebronJames(LebronJames.DATA_FOLDER, LebronJames.DATA_FILE);

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            Parent root = fxmlLoader.load();
            stage.setScene(new Scene(root, INITIAL_WIDTH, INITIAL_HEIGHT));
            stage.setTitle("LeBron James 👑 The King");
            stage.setMinWidth(MINIMUM_WIDTH);
            stage.setMinHeight(MINIMUM_HEIGHT);

            // The controller is created by the FXML loader, so the chatbot can
            // only be handed over after the file has been loaded.
            MainWindow controller = fxmlLoader.getController();
            controller.setLebronJames(lebronJames);
            controller.showWelcome();

            stage.show();
        } catch (IOException exception) {
            // A missing or broken FXML file is a packaging mistake, not
            // something the user can act on, so fail loudly during development
            // rather than opening an empty window.
            throw new IllegalStateException("Could not load the main window layout.", exception);
        }
    }
}
