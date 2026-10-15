package charliek.ui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Configures and displays the CharlieK JavaFX window.
 */
public class Main extends Application {
    /**
     * The initial width of the chat window.
     */
    private static final double INITIAL_WIDTH = 520.0;

    /**
     * The initial height of the chat window.
     */
    private static final double INITIAL_HEIGHT = 700.0;

    /**
     * The minimum width of the chat window.
     */
    private static final double MINIMUM_WIDTH = 400.0;

    /**
     * The minimum height of the chat window.
     */
    private static final double MINIMUM_HEIGHT = 500.0;

    /**
     * Loads the FXML view and shows the primary stage.
     *
     * @param stage the primary JavaFX window supplied by the runtime.
     * @throws IOException when the FXML view or stylesheet cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, INITIAL_WIDTH, INITIAL_HEIGHT);
        scene.getStylesheets().add(Main.class.getResource("/view/main.css").toExternalForm());

        stage.setTitle("CharlieK");
        stage.setMinWidth(MINIMUM_WIDTH);
        stage.setMinHeight(MINIMUM_HEIGHT);
        stage.setScene(scene);
        stage.show();
    }
}
