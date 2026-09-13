package xuan;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import xuan.ui.MainWindow;

/**
 * A GUI for the Xuan chatbot using FXML.
 */
public class Main extends Application {

    private final Xuan xuan = new Xuan();

    /**
     * Starts and displays the main JavaFX window.
     *
     * @param stage the primary stage for this application
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    Main.class.getResource("/view/MainWindow.fxml"));

            AnchorPane root = fxmlLoader.load();
            Scene scene = new Scene(root);

            stage.setTitle("Xuan");
            stage.setScene(scene);

            MainWindow controller = fxmlLoader.getController();
            controller.setXuan(xuan);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
