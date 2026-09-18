package bobby.gui;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import bobby.Bobby;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Displays Bobby's JavaFX graphical user interface from its FXML view.
 */
public class Main extends Application {
    private static final double WINDOW_WIDTH = 440.0;
    private static final double WINDOW_HEIGHT = 640.0;
    private static final double MINIMUM_WINDOW_WIDTH = 360.0;
    private static final double MINIMUM_WINDOW_HEIGHT = 480.0;

    private final Bobby bobby = new Bobby();

    @Override
    public void start(Stage stage) {
        URL mainWindow = Objects.requireNonNull(
                Main.class.getResource("/view/MainWindow.fxml"),
                "Missing main-window FXML resource");
        FXMLLoader fxmlLoader = new FXMLLoader(mainWindow);

        try {
            AnchorPane mainLayout = fxmlLoader.load();
            MainWindow controller = fxmlLoader.getController();
            controller.setBobby(bobby);

            stage.setScene(new Scene(mainLayout));
            stage.setTitle("Lord Bobby — Royal Steward of Duties");
            stage.setWidth(WINDOW_WIDTH);
            stage.setHeight(WINDOW_HEIGHT);
            stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
            stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
            stage.setResizable(true);
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load Bobby's main window.", e);
        }
    }
}
