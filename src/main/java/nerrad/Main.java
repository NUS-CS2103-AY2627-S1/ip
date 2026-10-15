package nerrad;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import nerrad.ui.MainWindow;

/**
 * Displays Nerrad's graphical user interface using JavaFX and FXML.
 */
public class Main extends Application {
    private static final String DATA_FILE_PATH = "data/nerrad.txt";

    /**
     * Loads and displays Nerrad's main window.
     *
     * @param stage Primary window provided by JavaFX.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Main.class.getResource("/css/main.css").toExternalForm());

            MainWindow mainWindow = fxmlLoader.getController();
            mainWindow.setNerrad(new Nerrad(DATA_FILE_PATH));

            stage.setTitle("Nerrad");
            stage.setScene(scene);
            stage.setMinWidth(440.0);
            stage.setMinHeight(360.0);
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load Nerrad's graphical interface.", exception);
        }
    }
}
