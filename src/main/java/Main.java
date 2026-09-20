import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Launches Loki's JavaFX graphical interface.
 */
public class Main extends Application {
    private static final double DEFAULT_WINDOW_HEIGHT = 680.0;
    private static final double DEFAULT_WINDOW_WIDTH = 520.0;
    private static final double MINIMUM_WINDOW_HEIGHT = 480.0;
    private static final double MINIMUM_WINDOW_WIDTH = 360.0;

    /**
     * Loads the FXML view and displays the primary Loki window.
     *
     * @param stage the primary stage provided by JavaFX.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            MainWindow mainWindow = fxmlLoader.getController();

            mainWindow.setLogic(new loki.logic.Logic());
            scene.getStylesheets().add(Main.class.getResource("/css/main.css").toExternalForm());
            scene.getStylesheets().add(Main.class.getResource("/css/dialog-box.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Loki — Task Forge");
            stage.setResizable(true);
            stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
            stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
            stage.setWidth(DEFAULT_WINDOW_WIDTH);
            stage.setHeight(DEFAULT_WINDOW_HEIGHT);
            stage.setOnCloseRequest(event -> mainWindow.saveData());
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the Loki GUI", exception);
        }
    }
}
