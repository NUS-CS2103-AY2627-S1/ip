package botzilla.gui;

import java.io.IOException;

import botzilla.Botzilla;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for the Botzilla chatbot, built using FXML.
 */
public class Main extends Application {

    private static final String SAVE_FILE_PATH = "./data/botzilla.txt";

    private Botzilla botzilla = new Botzilla(SAVE_FILE_PATH);

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setTitle("Botzilla");
            stage.setMinHeight(220.0);
            stage.setMinWidth(417.0);
            fxmlLoader.<MainWindow>getController().setBotzilla(botzilla);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load MainWindow.fxml", e);
        }
    }
}
