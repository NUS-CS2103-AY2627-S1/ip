package verity.gui;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import verity.Verity;

/**
 * Starts Verity's JavaFX graphical user interface.
 */
public class Main extends Application {
    private final Verity verity =
            new Verity(Path.of("data", "verity.txt"));

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(
                    Main.class.getResource("/view/MainWindow.fxml"),
                    "Missing MainWindow.fxml resource."));
            Parent mainLayout = fxmlLoader.load();
            MainWindow mainWindow = fxmlLoader.getController();
            mainWindow.setVerity(verity);

            stage.setScene(new Scene(mainLayout));
            stage.setTitle("Verity");
            stage.setResizable(true);
            stage.setMinHeight(320.0);
            stage.setMinWidth(360.0);
            stage.show();
        } catch (IOException | RuntimeException exception) {
            throw new IllegalStateException(
                    "Could not load MainWindow.fxml.", exception);
        }
    }
}
