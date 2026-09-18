package student.project.bob.util;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import student.project.bob.Bob;
import student.project.bob.fx.MainWindow;

/**
 * A GUI for Bob using FXML.
 */
public class Main extends Application {

    private Bob bob = new Bob();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setTitle("Bob the Task Manager");
            stage.setMinHeight(260);
            stage.setMinWidth(360);
            fxmlLoader.<MainWindow>getController().setBob(bob);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
