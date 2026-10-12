package echo;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for E.C.H.O. using FXML.
 */
public class Main extends Application {
    private final Echo echo = new Echo();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setTitle("E.C.H.O.");
            stage.setWidth(950);
            stage.setHeight(700);
            stage.setMinWidth(900);
            stage.setMinHeight(500);
            fxmlLoader.<MainWindow>getController().setEcho(echo);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
