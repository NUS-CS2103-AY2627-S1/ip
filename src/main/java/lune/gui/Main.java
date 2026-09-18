package lune.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lune.Lune;

/**
 * The JavaFX GUI entry point for Lune: loads MainWindow.fxml, injects a
 * Lune instance into its controller, and shows the chat window. Run via
 * Launcher, not directly.
 */
public class Main extends Application {
    private final Lune lune = new Lune();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane mainLayout = fxmlLoader.load();
            Scene scene = new Scene(mainLayout);

            stage.setTitle("Lune");
            stage.setMinHeight(220.0);
            stage.setMinWidth(417.0);

            fxmlLoader.<MainWindow>getController().setLune(lune);

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
