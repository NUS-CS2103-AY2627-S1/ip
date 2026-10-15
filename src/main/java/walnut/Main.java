package walnut;

import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Represents the main JavaFX application for Walnut.
 *
 * <p>This class initializes the application window, loads the main FXML layout,
 * and connects the {@link Walnut} instance to the {@link MainWindow} controller.</p>
 */
public class Main extends Application {

    private Walnut walnut = new Walnut();

    /**
     * Starts the JavaFX application and displays the main window.
     *
     * @param stage the primary stage for the application
     */
    @Override
    public void start(Stage stage) {
        // Configure global logger for the application to ensure consistent logging behavior
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.INFO);
        for (Handler h : rootLogger.getHandlers()) {
            h.setLevel(Level.INFO);
        }

        try {
            stage.setTitle("Walnut");
            stage.setMinHeight(220);
            stage.setMinWidth(417);
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setWalnut(walnut);
            stage.show();
        } catch (IOException e) {
            Logger.getLogger(Main.class.getName())
                    .log(Level.SEVERE, "Failed to load MainWindow FXML", e);
        }
    }
}
