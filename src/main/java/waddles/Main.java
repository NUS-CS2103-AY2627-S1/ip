package waddles;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A JavaFX GUI for the Waddles chatbot, launched via {@link Launcher}.
 *
 * <p>This class only wires up the FXML-defined window and hands it a
 * {@link Waddles} instance; all chatbot behaviour still lives in
 * {@link Waddles}, {@link Parser}, and {@link TaskList} exactly as it does
 * for the CLI.
 */
public class Main extends Application {
    private static final String DATA_FILE_PATH = "./data/waddles.txt";

    private Waddles waddles = new Waddles(DATA_FILE_PATH);

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            scene.getStylesheets().add(Main.class.getResource("/css/main.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Waddles ✨");
            // Keep the window resizable (the default), but stop it from being resized so small
            // that the chat bubbles and input bar no longer fit sensibly.
            stage.setMinWidth(360);
            stage.setMinHeight(220);
            fxmlLoader.<MainWindow>getController().setWaddles(waddles);
            stage.show();
        } catch (IOException e) {
            // As in DialogBox, the FXML layout is a bundled resource: failing to load it means
            // the app cannot show a window at all, so fail fast instead of exiting silently.
            throw new IllegalStateException("Failed to load MainWindow.fxml", e);
        }
    }
}
