package walle;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import walle.gui.MainWindow;

/**
 * A JavaFX GUI for WALLE, loaded from FXML.
 */
public class Main extends Application {

    private final WALLE walle = new WALLE();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setTitle("WALLE");
            fxmlLoader.<MainWindow>getController().setWalle(walle);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
