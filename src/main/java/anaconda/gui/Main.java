package anaconda.gui;

import java.io.IOException;

import anaconda.Anaconda;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Starts the JavaFX interface for Anaconda using FXML.
 */
public class Main extends Application {
    private final Anaconda anaconda = new Anaconda();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/View/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Anaconda");
            stage.setMinHeight(220);
            stage.setMinWidth(417);
            fxmlLoader.<MainWindow>getController().setAnaconda(anaconda);
            stage.show();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
