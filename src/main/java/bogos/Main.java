package bogos;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Starts the Bogos JavaFX user interface.
 */
public class Main extends Application {
    private final Bogos bogos = new Bogos();

    /**
     * Creates and shows the Bogos application window.
     *
     * @param stage Primary JavaFX stage.
     */
    @Override
    public void start(Stage stage) {
        try {
            bogos.showConsoleWelcome();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();
            Scene scene = new Scene(anchorPane);
            stage.setTitle("Bogos");
            stage.setMinWidth(360);
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setBogos(bogos);
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Could not load the Bogos user interface.", e);
        }
    }
}
