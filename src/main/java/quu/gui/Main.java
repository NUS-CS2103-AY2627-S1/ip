package quu.gui;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import quu.Quu;

/**
 * Displays the Quu chatbot in a JavaFX window loaded from FXML.
 */
public class Main extends Application {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static final double MINIMUM_WINDOW_WIDTH = 360;
    private static final double MINIMUM_WINDOW_HEIGHT = 420;

    private final Quu quu = new Quu();

    @Override
    public void start(Stage stage) throws IOException {
        LOGGER.info("Starting Quu");

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        Parent root;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            // Rethrown so JavaFX still fails loudly; logged first because the stack trace
            // JavaFX prints on its own does not say which layout could not be loaded.
            LOGGER.log(Level.SEVERE, "Unable to load /view/MainWindow.fxml", e);
            throw e;
        }
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("Quu");
        // Only minimums are set: capping the width would stop the window being resized.
        stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
        stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);

        MainWindow mainWindow = fxmlLoader.getController();
        assert mainWindow != null : "MainWindow.fxml names quu.gui.MainWindow as its controller";
        mainWindow.setQuu(quu);
        stage.show();

        LOGGER.info("Main window shown");
    }
}
