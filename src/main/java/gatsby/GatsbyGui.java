package gatsby;

import java.io.IOException;

import gatsby.ui.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/** Provides a JavaFX chat window for Gatsby. */
public class GatsbyGui extends Application {
    /** The default width of the chat window. */
    private static final double WINDOW_WIDTH = 560.0;

    /** The default height of the chat window. */
    private static final double WINDOW_HEIGHT = 720.0;

    /** The narrowest width that still leaves room for the composer. */
    private static final double MIN_WINDOW_WIDTH = 380.0;

    /** The shortest height that still leaves room for the conversation. */
    private static final double MIN_WINDOW_HEIGHT = 460.0;

    /**
     * Loads and displays the FXML-defined Gatsby chat window.
     *
     * @param stage the primary JavaFX window
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(GatsbyGui.class.getResource("/view/MainWindow.fxml"));
            BorderPane root = fxmlLoader.load();
            MainWindow mainWindow = fxmlLoader.getController();
            mainWindow.setGatsby(new Gatsby());

            Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
            stage.setTitle("Gatsby");
            stage.setMinWidth(MIN_WINDOW_WIDTH);
            stage.setMinHeight(MIN_WINDOW_HEIGHT);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load Gatsby's JavaFX layout", e);
        }
    }
}
