package wally;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Starts the JavaFX application for Wally.
 */
public class Main extends Application {
    private final Wally wally = new Wally();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();
            Scene scene = new Scene(anchorPane);
            stage.setScene(scene);
            stage.setTitle("Wally");
            stage.setWidth(1280);
            stage.setHeight(720);
            fxmlLoader.<MainWindow>getController().setWally(wally);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
