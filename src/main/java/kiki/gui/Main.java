package kiki.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * JavaFX entry point for Kiki's graphical user interface.
 */
public class Main extends Application {
    /**
     * Creates the main window and shows it.
     *
     * @param stage The primary JavaFX window.
     * @throws IOException If the FXML layout cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane root = fxmlLoader.load();
        Scene scene = new Scene(root);

        stage.setTitle("Kiki");
        stage.setScene(scene);
        stage.setMinHeight(520);
        stage.setMinWidth(420);
        stage.show();
    }
}
