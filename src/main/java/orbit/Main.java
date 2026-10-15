package orbit;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import orbit.ui.MainWindow;

/**
 * Loads and displays Orbit's JavaFX interface.
 */
public class Main extends Application {
    private static final String STORAGE_PATH = "./data/orbit.txt";

    /**
     * Creates the JavaFX application entry point.
     */
    public Main() {
    }

    /**
     * Creates Orbit's main window and connects it to the chatbot.
     *
     * @param stage primary stage supplied by JavaFX
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane mainLayout = loader.load();
            loader.<MainWindow>getController().setOrbit(new Orbit(STORAGE_PATH));

            stage.setTitle("Orbit | Mission Control");
            stage.setMinHeight(500.0);
            stage.setMinWidth(440.0);
            stage.setScene(new Scene(mainLayout));
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load Orbit's main window.", e);
        }
    }
}
