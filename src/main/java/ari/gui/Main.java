package ari.gui;

import java.io.IOException;

import ari.Ari;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Configures and displays Ari's main JavaFX window.
 */
public class Main extends Application {
    private static final String DATA_FILE_PATH = "data/ari.txt";

    private final Ari ari = new Ari(DATA_FILE_PATH);

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    Main.class.getResource("/view/MainWindow.fxml")
            );
            AnchorPane root = fxmlLoader.load();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle("Ari");
            stage.setMinHeight(480);
            stage.setMinWidth(420);
            // Accepted mutations are already persisted; closing never overwrites a failed load.
            fxmlLoader.<MainWindow>getController().setAri(this.ari);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
