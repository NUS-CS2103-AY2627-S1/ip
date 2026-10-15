package chudgpt;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/** Starts and configures the ChudGPT JavaFX application. */
public class Main extends Application {

    private ChudGpt chud = new ChudGpt("data/tasks.txt");

    @Override
    public void start(Stage stage) throws Exception {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setTitle("ChudGPT Task Manager");
            stage.setMinHeight(280);
            stage.setMinWidth(360);
            fxmlLoader.<MainWindow>getController().setChud(chud);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
