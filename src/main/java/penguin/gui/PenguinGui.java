package penguin.gui;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import penguin.Penguin;
import penguin.PenguinIdentity;

/**
 * A GUI for Penguin using FXML.
 */
public class PenguinGui extends Application {

    /** Chatbot instance used by the GUI. */
    private final Penguin penguin = new Penguin("./data/penguin.txt", false);

    /**
     * Creates the JavaFX application.
     */
    public PenguinGui() {
    }

    /**
     * Loads and displays the main Penguin window.
     *
     * @param stage primary JavaFX stage.
     * @throws IllegalStateException if the FXML or stylesheet cannot be loaded.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getRequiredResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            Scene scene = new Scene(root);
            String stylesheet = getRequiredResource(
                    "/view/style.css").toExternalForm();
            scene.getStylesheets().add(stylesheet);
            stage.setMinWidth(680);
            stage.setMinHeight(650);
            stage.setWidth(820);
            stage.setHeight(900);
            stage.setTitle(PenguinIdentity.WINDOW_TITLE);
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setPenguin(penguin);
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load MainWindow.fxml.", e);
        }
    }

    /**
     * Locates a required GUI resource on the classpath.
     *
     * @param resourcePath classpath path of the resource.
     * @return URL of the resource.
     * @throws IllegalStateException if the resource cannot be found.
     */
    private URL getRequiredResource(String resourcePath) {
        URL resource = PenguinGui.class.getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException(
                    "Unable to load GUI resource: " + resourcePath);
        }
        return resource;
    }
}
