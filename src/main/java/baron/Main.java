package baron;

import java.io.IOException;
import java.nio.file.Path;

import baron.controller.MainWindow;
import baron.core.Baron;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Starts the Baron graphical user interface defined with FXML.
 */
public class Main extends Application {

    private final Baron baron = new Baron(Path.of("data", "tasks.txt"));

    /**
     * Creates the JavaFX application.
     */
    public Main() {
    }

    /**
     * Creates and displays the primary Baron window.
     *
     * @param stage The primary application stage.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane mainLayout = fxmlLoader.load();
            Scene scene = new Scene(mainLayout);
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setBaron(baron);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
