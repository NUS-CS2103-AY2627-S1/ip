package zikiai.ui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import zikiai.Zikiai;

/**
 * Loads the tutorial-based JavaFX window and connects it to Zikiai.
 */
public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane root = loader.load();
        loader.<MainWindow>getController().setZikiai(new Zikiai());
        stage.setTitle("Zikiai");
        stage.setScene(new Scene(root));
        stage.setMinWidth(400);
        stage.setMinHeight(400);
        stage.show();
    }
}
