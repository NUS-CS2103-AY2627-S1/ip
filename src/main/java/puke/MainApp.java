package puke;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/** Displays the graphical Puke application. */
public class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = loader.load();
            Scene scene = new Scene(root);
            stage.setTitle("Puke - Task Sprite");
            stage.setScene(scene);
            loader.<MainWindow>getController().setPuke(new Puke());
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the Puke interface", e);
        }
    }
}
