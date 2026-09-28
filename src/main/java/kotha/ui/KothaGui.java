package kotha.ui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/** JavaFX entry point for Kotha's chatbot-style interface. */
public class KothaGui extends Application {
    @Override
    public void start(Stage stage) {
        try {
            AnchorPane root = new FXMLLoader(KothaGui.class.getResource("/view/MainWindow.fxml")).load();
            stage.setTitle("Kotha");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the Kotha interface.", exception);
        }
    }
}
