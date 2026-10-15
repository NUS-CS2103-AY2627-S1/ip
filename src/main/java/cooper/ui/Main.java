package cooper.ui;

import java.io.IOException;

import cooper.Cooper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for Cooper using FXML
 */
public class Main extends Application {
    /** The single Cooper instance shared with the main-window controller. */
    private final Cooper cooper = new Cooper();

    /**
     * Runs the GUI application.
     *
     * @param stage The primary stage for this application, onto which the application scene can be set.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setTitle("Cooper");
            stage.setScene(scene);
            stage.setMinHeight(300);
            stage.setMinWidth(320);
            stage.setResizable(true);
            // Injection keeps command logic in Cooper while the controller handles only GUI events.
            fxmlLoader.<MainWindow>getController().setCooper(cooper);
            stage.show();
        } catch (IOException e) {
            // The application cannot operate without its main layout, so fail with the original cause attached.
            throw new IllegalStateException("Unable to load the main window", e);
        }
    }
}
