package peter;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import peter.ui.MainWindow;

/**
 * A GUI for Peter using FXML.
 */
public class Main extends Application {
    private final Peter peter = new Peter("data/peter.txt");

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Peter");
            stage.setMinHeight(360);
            stage.setMinWidth(360);
            fxmlLoader.<MainWindow>getController().setPeter(peter);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
