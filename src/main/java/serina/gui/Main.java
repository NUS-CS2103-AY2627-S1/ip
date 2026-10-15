package serina.gui;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import serina.Serina;

/**
 * Displays Serina's graphical user interface.
 */
public class Main extends Application {
    private static final double INITIAL_HEIGHT = 720;
    private static final double INITIAL_WIDTH = 560;
    private static final double MINIMUM_HEIGHT = 520;
    private static final double MINIMUM_WIDTH = 420;

    private final Serina serina = new Serina();

    /**
     * Creates Serina's JavaFX application.
     */
    public Main() {
    }

    @Override
    public void start(Stage stage) throws IOException {
        URL mainWindowResource = requireResource("/view/MainWindow.fxml");
        URL stylesheetResource = requireResource("/css/main.css");

        FXMLLoader fxmlLoader = new FXMLLoader(mainWindowResource);
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, INITIAL_WIDTH, INITIAL_HEIGHT);
        scene.getStylesheets().add(stylesheetResource.toExternalForm());

        MainWindow controller = fxmlLoader.getController();
        controller.setSerina(serina);

        stage.setTitle("Serina");
        stage.setMinHeight(MINIMUM_HEIGHT);
        stage.setMinWidth(MINIMUM_WIDTH);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Returns an essential packaged resource with a clear startup failure if it is missing.
     *
     * @param resourcePath Classpath location of the resource.
     * @return Located resource URL.
     * @throws IOException If the application package is incomplete.
     */
    private static URL requireResource(String resourcePath) throws IOException {
        URL resource = Main.class.getResource(resourcePath);
        if (resource == null) {
            throw new IOException("Serina cannot start because this resource is missing: " + resourcePath);
        }
        return resource;
    }
}
