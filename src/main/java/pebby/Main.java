package pebby;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


/**
 * A GUI for Pebby using FXML.
 */
public class Main extends Application {

    private Pebby pebby = new Pebby();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setTitle("Pebby — Task Assistant");
            stage.setScene(scene);
            stage.setMinWidth(360);
            stage.setMinHeight(420);
            stage.setResizable(true);
            fxmlLoader.<MainWindow>getController().setPebby(pebby);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
