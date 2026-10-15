package bubu;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for Bubu using FXML.
 */
public class Main extends Application {
    private static final String APPLICATION_NAME = "Bubu";
    private static final String MAIN_LAYOUT_RESOURCE = "/view/MainWindow.fxml";
    private static final String STARTUP_ERROR_TITLE = "Bubu could not start";
    private static final String STARTUP_ERROR_MESSAGE =
            "The application layout could not be loaded. Please try starting Bubu again.";
    private static final double MINIMUM_WINDOW_HEIGHT = 220.0;
    private static final double MINIMUM_WINDOW_WIDTH = 417.0;

    private final Bubu bubu = new Bubu();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(MAIN_LAYOUT_RESOURCE));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setTitle(APPLICATION_NAME);
            stage.setScene(scene);
            stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
            stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
            fxmlLoader.<MainWindow>getController().setBubu(bubu);
            stage.show();
        } catch (IOException | RuntimeException exception) {
            showStartupError(exception);
        }
    }

    /**
     * Shows a user-friendly message when the application cannot load its GUI.
     *
     * @param exception failure that prevented the application from starting.
     */
    private void showStartupError(Exception exception) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(APPLICATION_NAME);
        alert.setHeaderText(STARTUP_ERROR_TITLE);
        alert.setContentText(STARTUP_ERROR_MESSAGE);
        alert.showAndWait();
    }
}
