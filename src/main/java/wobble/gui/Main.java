package wobble.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** Creates the main window for the FXML-based Wobble interface. */
public class Main extends Application {
    /** Loads the main FXML view and displays it in a window. */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            Parent root = loader.load();
            stage.setTitle("Wobble — Task Companion");
            stage.setScene(new Scene(root));
            stage.setMinWidth(420);
            stage.setMinHeight(420);
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the Wobble interface.", exception);
        }
    }
}
