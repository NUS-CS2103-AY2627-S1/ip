package panda.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import panda.Panda;

/**
 * Starts Panda's JavaFX user interface.
 */
public class Main extends Application {
    /**
     * Loads and displays Panda's main window.
     *
     * @param stage the primary JavaFX stage.
     * @throws IOException if the main-window resource cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        Panda panda = new Panda();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        BorderPane root = fxmlLoader.load();

        fxmlLoader.<MainWindow>getController().setPanda(panda);
        stage.setMinHeight(320);
        stage.setMinWidth(360);
        stage.setResizable(true);
        stage.setTitle("Panda");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
