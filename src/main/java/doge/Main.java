package doge;

import java.io.IOException;

import doge.exception.DogeException;
import doge.ui.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Provides the JavaFX GUI for Doge using FXML.
 */
public class Main extends Application {

    private final Doge doge = new Doge();

    @Override
    public void start(Stage stage) {
        try {
            stage.setTitle("Doge: Much Task, Very Organised");
            stage.setMinHeight(500);
            stage.setMinWidth(600);
            stage.setWidth(1000);
            stage.setHeight(760);
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setDoge(doge); // inject the Doge instance
            stage.setOnCloseRequest(event -> saveTasksOnClose());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveTasksOnClose() {
        try {
            doge.saveTasks();
        } catch (DogeException e) {
            e.printStackTrace();
        }
    }
}
