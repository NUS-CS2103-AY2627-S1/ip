package herta;

import java.io.IOException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Provides the JavaFX user interface entry point for Herta.
 */
public class Main extends Application {
    private static final double MINIMUM_WINDOW_HEIGHT = 450.0;
    private static final double MINIMUM_WINDOW_WIDTH = 400.0;
    private static final String STARTUP_ERROR = "Application files are incomplete; reinstall Herta.";
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private final Herta herta = new Herta();

    @Override
    public void start(Stage stage) {
        try {
            validateApplicationResources();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane mainWindow = fxmlLoader.load();
            Scene scene = new Scene(mainWindow);

            stage.setTitle("Herta");
            URL iconResource = requireResource(MainWindow.HERTA_IMAGE_RESOURCE);
            Image appIcon = new Image(iconResource.toExternalForm());
            stage.getIcons().add(appIcon);
            stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
            stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setHerta(herta);
            stage.show();
        } catch (IOException | RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Unable to start the graphical interface.", e);
            showStartupError(stage);
        }
    }

    /** Checks every resource referenced by the GUI before constructing any controls. */
    private void validateApplicationResources() {
        String[] requiredResourcePaths = {"/view/MainWindow.fxml", "/view/DialogBox.fxml",
            "/css/main.css", "/css/dialog-box.css", MainWindow.HERTA_IMAGE_RESOURCE};
        for (String resourcePath : requiredResourcePaths) {
            requireResource(resourcePath);
        }
    }

    /** Returns a required application resource or reports an incomplete installation. */
    private static URL requireResource(String resourcePath) {
        URL resource = Main.class.getResource(resourcePath);
        if (resource == null) {
            throw new MissingResourceException(STARTUP_ERROR, Main.class.getName(), resourcePath);
        }
        return resource;
    }

    /** Presents a minimal error scene when the normal GUI resources cannot be loaded. */
    private void showStartupError(Stage stage) {
        stage.setTitle("Herta");
        stage.setScene(new Scene(new Label(STARTUP_ERROR)));
        stage.show();
    }
}
