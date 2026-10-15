package zen.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import zen.Zen;

/**
 * A GUI for using FXML.
 */
public class Main extends Application {

    private final Zen zen = new Zen("data/task_list.txt");

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setMinHeight(600);
            stage.setMinWidth(400);
            stage.setTitle("Zen");
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setZen(zen);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
