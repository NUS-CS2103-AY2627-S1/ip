package judey;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import judey.gui.MainWindow;

/**
 * A GUI for Judey using FXML.
 */
public class Main extends Application {
    private static final String STARTUP_ERROR_TITLE = "Judey could not start";
    private static final String STARTUP_ERROR_HEADER = "Judey could not load its user interface.";
    private static final String STARTUP_ERROR_CONTENT = "Please restart the application. If the problem continues, "
            + "reinstall Judey.";

    private final Judey judey = new Judey();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setTitle("Judey");
            stage.setWidth(500);
            stage.setHeight(700);
            stage.setMinHeight(220);
            stage.setMinWidth(417);
            fxmlLoader.<MainWindow>getController().setJudey(judey);  // inject the Judey instance
            stage.show();
        } catch (IOException e) {
            showStartupError(stage);
        }
    }

    /** Shows a user-friendly message when the main GUI cannot be loaded. */
    static void showStartupError(Stage stage) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(STARTUP_ERROR_TITLE);
        alert.setHeaderText(STARTUP_ERROR_HEADER);
        alert.setContentText(STARTUP_ERROR_CONTENT);
        alert.initOwner(stage);
        alert.showAndWait();
    }

    /** Returns the title used by the startup failure alert. */
    static String startupErrorTitle() {
        return STARTUP_ERROR_TITLE;
    }

    /** Returns the recovery guidance used by the startup failure alert. */
    static String startupErrorContent() {
        return STARTUP_ERROR_CONTENT;
    }
}
