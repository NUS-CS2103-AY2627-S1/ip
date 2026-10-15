package quackers;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * The JavaFX application for Quackers.
 *
 * <p>Its only jobs are to load the window layout from FXML, give the controller a
 * {@link Quackers} instance to talk to, and show the window.
 */
public class Main extends Application {

    private final Quackers quackers = new Quackers();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            stage.setScene(new Scene(root));
            stage.setTitle("Quackers");
            stage.setMinHeight(300.0);
            stage.setMinWidth(417.0);
            fxmlLoader.<MainWindow>getController().setQuackers(quackers); // inject the Quackers instance
            stage.show();
        } catch (IOException error) {
            error.printStackTrace();
        }
    }
}
