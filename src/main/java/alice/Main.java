package alice;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * A JavaFX GUI for the Alice chatbot, laid out with FXML.
 *
 * <p>This class only wires up the window (loading {@code MainWindow.fxml}
 * and handing the {@link Alice} instance to its controller); the actual
 * chatbot behaviour lives in {@link Alice#getResponse(String)}, so the GUI
 * and the text UI in {@link Alice#main} share the same logic.
 */
public class Main extends Application {
    private final Alice alice = new Alice();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            HBox root = fxmlLoader.load();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle("Alice");
            stage.setMinWidth(360);
            stage.setMinHeight(260);

            MainWindow controller = fxmlLoader.getController();
            controller.setAlice(alice);

            stage.show();
        } catch (IOException e) {
            // The FXML file is bundled as a resource, so this should only
            // happen if it is accidentally deleted or renamed.
            throw new IllegalStateException("Could not load MainWindow.fxml", e);
        }
    }

    /**
     * Launches the JavaFX application (called by {@link Launcher}).
     *
     * @param args command-line arguments (not used).
     */
    public static void main(String[] args) {
        launch(args);
    }
}
