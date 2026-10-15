package dukey.gui;

import java.io.IOException;

import dukey.Dukey;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Runs the JavaFX GUI for Dukey.
 */
public class Main extends Application {
    private final Dukey dukey = new Dukey("src/main/data/dukey.txt", false);

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            fxmlLoader.<MainWindow>getController().setDukey(this.dukey);

            Scene scene = new Scene(root);
            stage.setTitle("Dukey");
            stage.setMinHeight(360);
            stage.setMinWidth(420);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
