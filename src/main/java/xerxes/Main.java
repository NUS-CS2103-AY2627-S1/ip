package xerxes;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for Xerxes using FXML.
 */
public class Main extends Application {

    private Xerxes xerxes = new Xerxes();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);

            stage.setTitle("Xerxes Task Manager");
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setXerxes(xerxes); // inject the Xerxes instance
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load MainWindow.fxml", e);
        }
    }
}
