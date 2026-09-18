package habi;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** Loads and displays HABI's JavaFX interface. */
public class Main extends Application {
    private static final String MAIN_WINDOW_RESOURCE = "/view/MainWindow.fxml";
    private static final String STYLESHEET_RESOURCE = "/view/habi.css";

    /**
     * Creates the main scene and injects HABI's command handler.
     *
     * @param stage Primary JavaFX stage.
     * @throws IOException If the main-window FXML cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(requireResource(MAIN_WINDOW_RESOURCE));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(requireResource(STYLESHEET_RESOURCE).toExternalForm());

        MainWindow controller = loader.getController();
        controller.setHabi(new Habi(Path.of("data", "habi.txt")));

        stage.setTitle("HABI");
        stage.setMinWidth(520);
        stage.setMinHeight(640);
        stage.setScene(scene);
        stage.show();
        Platform.runLater(controller::focusInput);
    }

    private static URL requireResource(String path) {
        URL resource = Main.class.getResource(path);
        if (resource == null) {
            throw new IllegalStateException("Missing JavaFX resource: " + path);
        }
        return resource;
    }
}
