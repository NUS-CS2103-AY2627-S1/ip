package milo;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import milo.ui.MainWindow;

/** A JavaFX GUI for Milo using FXML. */
public class Main extends Application {
    private final Milo milo = new Milo();

    /** Loads and shows Milo's main window. */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();
            Scene scene = new Scene(anchorPane);
            stage.setTitle("Milo");
            stage.setScene(scene);
            stage.setResizable(true);
            stage.setMinWidth(380);
            stage.setMinHeight(360);
            fxmlLoader.<MainWindow>getController().setMilo(milo);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
