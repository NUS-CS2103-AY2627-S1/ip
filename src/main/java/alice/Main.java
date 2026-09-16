package alice;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Provides the JavaFX window for the Alice chatbot.
 */
public class Main extends Application {
    private static final String DEFAULT_FILE_PATH = "./data/alice.txt";

    /**
     * Creates and displays the primary Alice window.
     *
     * @param stage The primary JavaFX stage.
     * @throws IOException if the FXML layout cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("Main.fxml"));
        Scene scene = new Scene(loader.load(), 900, 700);
        stage.setTitle("Alice | Your task companion");
        stage.setMinWidth(600);
        stage.setMinHeight(500);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Returns the default file used to persist tasks.
     *
     * @return The default task storage path.
     */
    public static String getDefaultFilePath() {
        return DEFAULT_FILE_PATH;
    }
}
