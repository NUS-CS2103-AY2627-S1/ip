package nori;

import java.io.IOException;
import java.util.Objects;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nori.ui.MainWindow;

/**
 * Displays Nori's JavaFX user interface.
 */
public class Main extends Application {
    private final Nori nori = new Nori();

    /**
     * Creates and displays Nori's primary window.
     *
     * @param stage primary stage supplied by JavaFX.
    */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(
                    Main.class.getResource("/css/main.css")).toExternalForm());

            MainWindow mainWindow = fxmlLoader.getController();
            mainWindow.setNori(nori);

            stage.setTitle("Nori");
            stage.setMinHeight(500);
            stage.setMinWidth(400);
            stage.setScene(scene);
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load Nori's user interface.", exception);
        }
    }
}
