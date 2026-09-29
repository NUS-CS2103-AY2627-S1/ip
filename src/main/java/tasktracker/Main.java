package tasktracker;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Sets up and displays the graphical user interface for {@code TaskTracker} using FXML.
 */
public class Main extends Application {

    private final TaskTracker taskTracker = new TaskTracker();

    /**
     * Initializes and displays the primary application stage with FXML views.
     *
     * @param stage Primary stage container provided by the JavaFX runtime.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);

            // Pass the TaskTracker instance into the controller
            fxmlLoader.<MainWindow>getController().setTaskTracker(taskTracker);

            stage.setTitle("TaskTracker");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
