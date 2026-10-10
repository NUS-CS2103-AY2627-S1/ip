package zsiggy;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for Zsiggy using FXML.
 */
public class Main extends Application {

    private final Duke duke = new Duke();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader =
                    new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));

            AnchorPane mainWindow = fxmlLoader.load();
            Scene scene = new Scene(mainWindow);

            duke.loadTasks();

            fxmlLoader.<MainWindow>getController().setDuke(duke);

            stage.setScene(scene);
            stage.setTitle("Zsiggy");
            stage.setMinHeight(220);
            stage.setMinWidth(417);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
