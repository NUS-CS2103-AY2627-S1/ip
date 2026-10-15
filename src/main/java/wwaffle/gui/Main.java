package wwaffle.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import wwaffle.WWaffle;

/**
 * Loads and displays the WWaffle JavaFX interface.
 */
public class Main extends Application {
    private final WWaffle wwaffle = new WWaffle("./data/wwaffle.txt");

    /**
     * Creates the primary application window from its FXML layout.
     *
     * @param stage Primary JavaFX stage.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            BorderPane root = loader.load();
            MainWindow controller = loader.getController();
            controller.setWWaffle(wwaffle);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("WWaffle — Personal Task Manager");
            stage.setMinWidth(400);
            stage.setMinHeight(420);
            stage.show();
            controller.focusCommandBox();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the WWaffle interface.", e);
        }
    }
}
