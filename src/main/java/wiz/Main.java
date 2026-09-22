package wiz;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for Wiz using FXML.
 */
public class Main extends Application {

    private Wiz wiz = new Wiz();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setTitle("Wiz - Magical Task Manager");
            stage.setMinHeight(400);
            stage.setMinWidth(420);
            fxmlLoader.<MainWindow>getController().setWiz(wiz);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
