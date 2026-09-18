package sophon.ui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sophon.Sophon;

/**
 * A GUI for Sophon using FXML.
 */
public class Main extends Application {
    private final Sophon sophon = new Sophon("data", "sophon.txt");

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            MainWindow mainWindow = fxmlLoader.getController();
            mainWindow.setSophon(sophon, stage);

            stage.setTitle("Sophon · Deep Space Interface");
            stage.setResizable(true);
            stage.setMinHeight(620.0);
            stage.setMinWidth(460.0);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
