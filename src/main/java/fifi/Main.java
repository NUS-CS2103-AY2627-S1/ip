package fifi;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Loads the FXML interface and connects it to the Fifi chatbot.
 */
public class Main extends Application {
    private static final String SAVE_FILE_PATH = "data/fifi.txt";
    private final Fifi fifi;

    /**
     * Creates the graphical interface using the default saved task file.
     */
    public Main() {
        this(new Fifi(SAVE_FILE_PATH));
    }

    Main(Fifi fifi) {
        this.fifi = fifi;
    }

    /**
     * Loads the window layout and displays the primary JavaFX stage.
     *
     * @param stage main window provided by JavaFX
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = loader.load();
            loader.<MainWindow>getController().setFifi(fifi);
            Scene scene = new Scene(root);
            stage.setTitle("Fifi");
            stage.setMinWidth(400.0);
            stage.setMinHeight(600.0);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Could not load Fifi's window layout.", e);
        }
    }
}
