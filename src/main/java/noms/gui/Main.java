package noms.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import noms.Noms;

/**
 * The JavaFX entry point for Noms.
 *
 * It loads the main window from FXML and hands it a {@link Noms} instance, so
 * the GUI drives exactly the same parse-execute logic as the console app; only
 * the outer shell (a window instead of a {@code Scanner} loop) differs.
 */
public class Main extends Application {
    private static final double MIN_WINDOW_WIDTH = 400.0;
    private static final double MIN_WINDOW_HEIGHT = 450.0;

    private final Noms noms = new Noms();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Noms");
            stage.setMinWidth(MIN_WINDOW_WIDTH);
            stage.setMinHeight(MIN_WINDOW_HEIGHT);
            fxmlLoader.<MainWindow>getController().setNoms(noms);
            stage.show();
        } catch (IOException e) {
            // If the FXML cannot be loaded the GUI cannot start; surface the
            // cause on the console rather than failing silently.
            e.printStackTrace();
        }
    }
}
