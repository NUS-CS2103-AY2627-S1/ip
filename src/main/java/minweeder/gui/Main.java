package minweeder.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import minweeder.Minweeder;

/**
 * Entry point of the Minweeder GUI. Loads the main window layout from FXML,
 * injects a {@link Minweeder} instance into its controller, and displays it
 * in a {@link Stage}.
 */
public class Main extends Application {
    private final Minweeder minweeder = new Minweeder();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setTitle("Minweeder");
            fxmlLoader.<MainWindow>getController().setMinweeder(minweeder);
            stage.show();
        } catch (IOException e) {
            // The FXML layout is a bundled resource, not user input, so a failure here means
            // the jar is broken rather than something the user or caller can recover from.
            throw new IllegalStateException("Failed to load MainWindow.fxml", e);
        }
    }
}
