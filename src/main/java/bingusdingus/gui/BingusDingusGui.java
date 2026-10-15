package bingusdingus.gui;

import java.io.IOException;

import bingusdingus.BingusDingus;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Starts the Bingus Dingus graphical application.
 */
public class BingusDingusGui extends Application {

    private final BingusDingus bingusDingus = new BingusDingus();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(BingusDingusGui.class.getResource("/view/MainWindow.fxml"));
            BorderPane rootPane = fxmlLoader.load();
            Scene scene = new Scene(rootPane);
            stage.setScene(scene);
            stage.setTitle("Bingus Dingus");

            stage.setMinHeight(620);
            stage.setMinWidth(417);
            stage.setMaxHeight(700);
            stage.setMaxWidth(500);

            fxmlLoader.<MainWindow>getController().setBingusDingus(bingusDingus);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
