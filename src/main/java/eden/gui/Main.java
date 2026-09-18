package eden.gui;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;

import eden.Eden;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Loads and displays Eden's JavaFX user interface.
 */
public class Main extends Application {
    private static final String MAIN_WINDOW_RESOURCE = "/view/MainWindow.fxml";
    private static final Path DATA_FILE_PATH = Path.of("data", "eden.txt");

    private final Eden eden = new Eden(DATA_FILE_PATH);

    /**
     * Creates Eden's JavaFX application.
     */
    public Main() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage stage) throws IOException {
        URL mainWindowUrl = Objects.requireNonNull(
                Main.class.getResource(MAIN_WINDOW_RESOURCE),
                "Missing GUI resource: " + MAIN_WINDOW_RESOURCE);
        FXMLLoader fxmlLoader = new FXMLLoader(mainWindowUrl);
        Parent root = fxmlLoader.load();
        MainWindow mainWindow = fxmlLoader.getController();
        mainWindow.setEden(eden);

        stage.setTitle("Eden");
        stage.setMinWidth(420);
        stage.setMinHeight(520);
        stage.setResizable(true);
        stage.setScene(new Scene(root));
        stage.show();
    }
}
