package bob.ui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Starts Bob's graphical user interface.
 */
public class Main extends Application {
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = loader.load();
            stage.setScene(new Scene(root));
            stage.setTitle("Bob");
            stage.setMinHeight(220);
            stage.setMinWidth(417);
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the main window.", exception);
        }
    }
}
