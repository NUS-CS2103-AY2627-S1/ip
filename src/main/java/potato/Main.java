package potato;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import potato.ui.MainWindow;

/**
 * A GUI for Potato using FXML.
 */
public class Main extends Application {

    private final Potato potato = new Potato("./data/potato.txt");

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setTitle("Potato Task Manager");
            fxmlLoader.<MainWindow>getController().setPotato(potato);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}