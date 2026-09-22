import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Starts Mono's JavaFX application.
 */
public class Mono extends Application {
    /** Creates an application entry point. */
    public Mono() {
    }

    /**
     * Launches Mono's graphical user interface.
     *
     * @param args command-line arguments passed to JavaFX
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Loads and displays Mono's FXML-defined window.
     *
     * @param stage primary JavaFX stage
     * @throws IOException if the FXML layout cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        URL viewResource = Mono.class.getResource("/mono/ui/mono-view.fxml");
        assert viewResource != null : "The packaged FXML view must be available";
        FXMLLoader loader = new FXMLLoader(viewResource);
        Parent root = loader.load();
        Scene scene = new Scene(root, 900, 680);
        URL stylesheetResource = Mono.class.getResource("/mono/ui/mono.css");
        assert stylesheetResource != null : "The packaged stylesheet must be available";
        scene.getStylesheets().add(stylesheetResource.toExternalForm());

        stage.setTitle("Mono — Your Task Companion");
        stage.setMinWidth(640);
        stage.setMinHeight(520);
        stage.setScene(scene);
        stage.show();
    }
}
