package crystal.gui;

import java.io.IOException;
import java.nio.file.Path;

import crystal.Crystal;
import crystal.exception.CrystalException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Loads and displays Crystal's JavaFX window.
 */
public final class CrystalApplication extends Application {
    /** Default location of Crystal's persistent task data. */
    private static final Path DEFAULT_DATA_FILE = Path.of("data", "crystal.txt");

    /** Preferred width of the messaging window. */
    private static final double WINDOW_WIDTH = 560;

    /** Preferred height of the messaging window. */
    private static final double WINDOW_HEIGHT = 720;

    /**
     * Creates the JavaFX application instance used by the launcher.
     */
    public CrystalApplication() {
    }

    /**
     * Builds the primary scene and connects it to the Crystal application.
     *
     * @param stage primary JavaFX window.
     * @throws CrystalException if the FXML view cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws CrystalException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    CrystalApplication.class.getResource("/view/MainWindow.fxml"));
            Parent root = fxmlLoader.load();
            MainWindow mainWindow = fxmlLoader.getController();
            mainWindow.setCrystal(new Crystal(DEFAULT_DATA_FILE));

            stage.setTitle("Crystal");
            stage.setMinWidth(460);
            stage.setMinHeight(600);
            stage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
            stage.show();
        } catch (IOException exception) {
            CrystalException crystalException = new CrystalException(
                    "I couldn't open the Crystal window.");
            System.err.println(crystalException.getUserMessage());
            throw crystalException;
        }
    }
}
