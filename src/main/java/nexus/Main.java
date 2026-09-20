package nexus;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Displays the FXML-based user interface for Nexus.
 */
public class Main extends Application {
    private final Nexus nexus = new Nexus();

    /**
     * Loads and displays the main Nexus window.
     *
     * @param stage primary stage supplied by JavaFX.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            Parent mainLayout = fxmlLoader.load();
            Scene scene = new Scene(mainLayout);

            stage.setScene(scene);
            stage.setTitle("Nexus");
            stage.setResizable(true);
            stage.setMinHeight(520.0);
            stage.setMinWidth(420.0);
            fxmlLoader.<MainWindow>getController().setNexus(nexus);
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the Nexus interface.", exception);
        }
    }
}
