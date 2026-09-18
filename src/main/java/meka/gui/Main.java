package meka.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import meka.Meka;

/**
 * Displays MEKA's graphical chat interface.
 */
public class Main extends Application {
    private final Meka meka = new Meka();

    /**
     * Loads and displays the main MEKA window.
     *
     * @param stage primary JavaFX window.
     * @throws IOException if the FXML view cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        VBox root = fxmlLoader.load();
        fxmlLoader.<MainWindow>getController().setMeka(meka);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(Main.class.getResource("/view/Meka.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("MEKA");
        stage.setMinWidth(420);
        stage.setMinHeight(600);
        stage.show();
    }
}
