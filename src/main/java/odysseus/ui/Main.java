package odysseus.ui;

// Reused from https://se-education.org/guides/tutorials/javaFx.html with minor modifications.

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import odysseus.Odysseus;

/**
 * A GUI for Odysseus using FXML.
 */
public class Main extends Application {

    private Odysseus odysseus = new Odysseus(Odysseus.DEFAULT_STORAGE);
    private static final double MIN_WINDOW_HEIGHT = 220;
    private static final double MIN_WINDOW_WIDTH = 417;

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.
                    getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            scene.getStylesheets().add(
                    Main.class.getResource("/view/styles.css").toExternalForm());
            stage.setTitle("Odysseus");
            stage.setScene(scene);
            stage.setMinHeight(MIN_WINDOW_HEIGHT);
            stage.setMinWidth(MIN_WINDOW_WIDTH);
            fxmlLoader.<MainWindow>getController().setOdysseus(odysseus);  // inject the Odysseus instance
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
