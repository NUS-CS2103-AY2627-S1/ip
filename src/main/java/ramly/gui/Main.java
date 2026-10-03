package ramly.gui;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ramly.Ramly;

/** JavaFX entry point for the Ramly task manager. */
public class Main extends Application {
    private static final String DEFAULT_FILE_PATH = "./data/ramly.txt";

    /** Loads the FXML view and injects the shared Ramly command engine. */
    @Override
    public void start(Stage stage) {
        try {
            URL viewResource = Main.class.getResource("/view/MainWindow.fxml");
            if (viewResource == null) {
                throw new IllegalStateException("Missing GUI layout resource: /view/MainWindow.fxml");
            }
            FXMLLoader fxmlLoader = new FXMLLoader(viewResource);
            AnchorPane root = fxmlLoader.load();
            MainWindow controller = fxmlLoader.getController();
            controller.setRamly(new Ramly(DEFAULT_FILE_PATH));

            Scene scene = new Scene(root);
            stage.setTitle("Pip · Pocket Pathfinder");
            stage.setMinWidth(520);
            stage.setMinHeight(680);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the Pip GUI.", e);
        }
    }
}
