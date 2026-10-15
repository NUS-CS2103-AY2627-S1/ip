package yapatron;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for Yapatron using FXML
 */
public class Main extends Application {
    private Yapatron yapatron = new Yapatron(".data/yapatron.txt");

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setTitle("Yapatron :D");
            fxmlLoader.<MainWindow>getController().setYapatron(yapatron);

            stage.setWidth(800);
            stage.setHeight(700);
            stage.setMinWidth(400);
            stage.setMinHeight(300);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
