package megatron;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import megatron.ui.MainWindowController;

/**
 * Represents the main window of the Megatron JavaFX application.
 */
public class Main extends Application {
    private static final String WINDOW_TITLE = "Megatron \u2014 Task Manager";
    private static final String WINDOW_ICON_PATH = "/images/megatron-window-icon.png";
    private static final double WINDOW_WIDTH = 700;
    private static final double WINDOW_HEIGHT = 500;

    /**
     * Displays the main window loaded from its FXML layout.
     *
     * @param stage The primary JavaFX stage.
     * @throws IOException If the FXML layout cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        ApplicationConfiguration configuration = ApplicationConfiguration.fromArguments(
                getParameters().getRaw());
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        loader.setController(new MainWindowController(configuration.getStorageFilePath()));
        Scene scene = new Scene(loader.load(), WINDOW_WIDTH, WINDOW_HEIGHT);

        stage.setMinHeight(480);
        stage.setMinWidth(720);

        stage.setTitle(WINDOW_TITLE);
        stage.getIcons().add(new Image(Main.class.getResource(WINDOW_ICON_PATH).toExternalForm()));
        stage.setScene(scene);
        stage.show();
    }
}
