package emma.gui;

import emma.Emma;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Runs Emma as a window instead of a console conversation.
 */
public class Main extends Application {

    /** The window stays usable when dragged smaller, but not so small the text box vanishes. */
    private static final double MIN_WIDTH = 320;
    private static final double MIN_HEIGHT = 400;

    /**
     * Opens the window and hands it a chatbot to talk to.
     *
     * @param stage the window JavaFX provides.
     */
    @Override
    public void start(Stage stage) {
        MainWindow root = new MainWindow(new Emma(Emma.DEFAULT_SAVE_PATH));
        stage.setScene(new Scene(root, MainWindow.getWindowWidth(), MainWindow.getWindowHeight()));
        stage.setTitle("Emma");
        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);
        stage.show();
    }
}
