package gepit;

import java.io.IOException;

import gepit.ui.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Provides the JavaFX application for Gepit.
 */
public class Main extends Application {
    private final Gepit gepit = new Gepit();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane mainLayout = fxmlLoader.load();
            Scene scene = new Scene(mainLayout);

            stage.setScene(scene);
            stage.setTitle("Gepit");
            stage.setMinHeight(600);
            stage.setMinWidth(400);
            stage.setHeight(680);
            stage.setWidth(480);

            fxmlLoader.<MainWindow>getController().setGepit(gepit);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
