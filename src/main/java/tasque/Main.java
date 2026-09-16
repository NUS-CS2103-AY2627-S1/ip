package tasque;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Displays the Tasque graphical user interface.
 */
public class Main extends Application {
    private static final String DATA_FILE_PATH = "data/tasque.txt";

    private final Tasque tasque = new Tasque(DATA_FILE_PATH);

    /**
     * Creates the Tasque JavaFX application.
     */
    public Main() {
    }

    /**
     * Loads the main FXML view and displays it in the primary stage.
     *
     * @param stage Primary stage supplied by JavaFX.
     * @throws IOException If the main FXML view cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane root = fxmlLoader.load();
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("Tasque");
        stage.setMinHeight(360.0);
        stage.setMinWidth(440.0);
        fxmlLoader.<MainWindow>getController().setTasque(this.tasque);
        stage.show();
    }
}
