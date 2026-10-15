package dude.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX window for the DUDE task manager.
 */
public class Main extends Application {
    /**
     * Creates the JavaFX application.
     */
    public Main() {
    }

    /**
     * Builds and displays the main application window.
     *
     * @param stage Primary JavaFX stage.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("MainWindow.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 900, 650);
        scene.getStylesheets().add(Main.class.getResource("main.css").toExternalForm());
        stage.setTitle("DUDE");
        stage.setScene(scene);
        stage.show();
    }
}
