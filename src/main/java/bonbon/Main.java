package bonbon;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for BonBon using FXML.
 */
public class Main extends Application {

    private BonBon bonBon = new BonBon();

    @Override
    public void start(Stage stage) {
        try {
            bonBon.loadTasks();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.show();
            stage.setResizable(true);

            stage.setMinHeight(600.0);
            stage.setMinWidth(400.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

