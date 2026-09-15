package chatty.gui;

import java.io.IOException;
import java.nio.file.Path;

import chatty.Chatty;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Loads and displays Chatty's JavaFX user interface.
 */
public class Main extends Application {
    /** Default task data file shared with the console application. */
    private static final Path DATA_FILE_PATH = Path.of("data", "chatty.txt");

    /**
     * Loads the FXML view, injects Chatty, and displays the primary window.
     *
     * @param stage primary JavaFX stage.
     * @throws IOException if the main-window FXML resource cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane root = fxmlLoader.load();
        MainWindow mainWindow = fxmlLoader.getController();
        mainWindow.setChatty(new Chatty(DATA_FILE_PATH));

        stage.setTitle("Chatty — Task Mission Control");
        stage.setMinWidth(380);
        stage.setMinHeight(480);
        stage.setScene(new Scene(root));
        stage.show();
    }
}
