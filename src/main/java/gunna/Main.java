package gunna;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * A GUI for Gunna using FXML.
 */
public class Main extends Application {

    private Gunna gunna = new Gunna("data/duke.txt");

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Main.class.getResource("/view/main.css").toExternalForm());
            stage.setScene(scene);
            stage.setMinWidth(360);
            stage.setMinHeight(420);
            stage.setTitle("Gunna | Mission Control");
            fxmlLoader.<MainWindow>getController().setGunna(gunna);
            fxmlLoader.<MainWindow>getController().showWelcomeMessage();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
