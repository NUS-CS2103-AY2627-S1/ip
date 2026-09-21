package yqr.gui;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import yqr.Duke;

/**
 * Displays the yqr chatbot as a JavaFX application.
 */
public class Main extends Application {
    private static final double WINDOW_WIDTH = 720;
    private static final double WINDOW_HEIGHT = 640;
    private static final double MIN_WINDOW_WIDTH = 520;
    private static final double MIN_WINDOW_HEIGHT = 480;

    private final Duke duke = new Duke();

    /**
     * Loads the main FXML view and shows it in the primary stage.
     *
     * @param stage primary JavaFX stage.
     */
    @Override
    public void start(Stage stage) {
        assert stage != null : "JavaFX should provide a primary stage";

        try {
            URL mainWindowResource = Main.class.getResource("/view/MainWindow.fxml");
            assert mainWindowResource != null : "MainWindow.fxml should be available";

            FXMLLoader loader = new FXMLLoader(mainWindowResource);
            Parent root = loader.load();
            MainWindow controller = loader.getController();
            assert root != null : "MainWindow.fxml should define a root node";
            assert controller != null : "MainWindow.fxml should define a controller";
            controller.setDuke(duke);

            Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
            URL stylesheetResource = Main.class.getResource("/css/main.css");
            assert stylesheetResource != null : "main.css should be available";
            scene.getStylesheets().add(stylesheetResource.toExternalForm());

            stage.setTitle("yqr");
            stage.setMinWidth(MIN_WINDOW_WIDTH);
            stage.setMinHeight(MIN_WINDOW_HEIGHT);
            stage.setResizable(true);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the yqr interface", e);
        }
    }
}
