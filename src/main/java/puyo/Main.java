package puyo;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for Puyo using FXML.
 */
public class Main extends Application {

    private Puyo puyo = new Puyo("data/puyo.txt");

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            scene.getStylesheets().add(this.getClass().getResource("/view/main.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Puyo");
            fxmlLoader.<MainWindow>getController().setPuyo(puyo);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
