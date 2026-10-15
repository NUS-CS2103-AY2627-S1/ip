package tuesday.ui;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.function.Consumer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Creates the Tuesday application window.
 */
public class Ui extends Application {
    /**
     * Opens the application at its preferred size and sets the minimum usable window size.
     * Displays the Tuesday name and icon in the window title bar.
     *
     * @param stage The primary application window supplied by JavaFX.
     */
    @Override
    public void start(Stage stage) {
        try {
            AnchorPane root = loadMainWindow(Ui.class.getResource("/view/MainWindow.fxml"),
                    getHostServices()::showDocument);
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle("Tuesday");
            URL icon = Ui.class.getResource("/images/Tuesday.png");
            if (icon == null) {
                throw new IllegalStateException("The Tuesday icon resource is missing.");
            }
            stage.getIcons().add(new Image(icon.toExternalForm()));
            stage.show();
            configureMinimumWindowSize(stage, scene, root);
        } catch (IOException | UncheckedIOException | IllegalStateException e) {
            UiErrors.showFatal("Tuesday could not load its interface or saved tasks.", e);
        }
    }

    /**
     * Loads the main layout only when the required resource is present.
     */
    static AnchorPane loadMainWindow(URL resource) throws IOException {
        return loadMainWindow(resource, null);
    }

    /**
     * Loads the controller and supplies browser access before accepting input.
     */
    static AnchorPane loadMainWindow(URL resource, Consumer<String> linkOpener) throws IOException {
        if (resource == null) {
            throw new IllegalStateException("The main window layout resource is missing.");
        }
        FXMLLoader loader = new FXMLLoader(resource);
        AnchorPane root = loader.load();
        MainWindow controller = loader.getController();
        controller.setLinkOpener(linkOpener);
        return root;
    }

    /**
     * Adds the actual window borders to the minimum usable content size.
     * This must run after showing the stage, when its decoration sizes are available.
     *
     * @param stage The displayed application window.
     * @param scene The scene containing the application content.
     * @param root The root pane defining the minimum content dimensions.
     */
    private void configureMinimumWindowSize(Stage stage, Scene scene, AnchorPane root) {
        double decorationWidth = Math.max(0, stage.getWidth() - scene.getWidth());
        double decorationHeight = Math.max(0, stage.getHeight() - scene.getHeight());

        stage.setMinWidth(root.getMinWidth() + decorationWidth);
        stage.setMinHeight(root.getMinHeight() + decorationHeight);
    }
}
