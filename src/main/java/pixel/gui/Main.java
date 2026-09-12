package pixel.gui;

import java.io.IOException;
import java.nio.file.Path;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import pixel.Pixel;

/**
 * Starts the Pixel graphical user interface.
 */
public class Main extends Application {
    private static final Path DATA_FILE = Path.of("data", "pixel.txt");

    private final Pixel pixel = new Pixel(DATA_FILE);

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane mainWindow = fxmlLoader.load();
            MainWindow controller = fxmlLoader.getController();
            controller.setPixel(pixel);

            stage.setTitle("Pixel // Quest Console");
            stage.setScene(new Scene(mainWindow));
            stage.setMinWidth(420);
            stage.setMinHeight(480);
            stage.setWidth(560);
            stage.setHeight(680);
            stage.setResizable(true);
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the Pixel GUI.", exception);
        }
    }
}
