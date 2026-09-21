package echo;

import java.io.IOException;
import java.net.URL;

import echo.ui.components.MainWindow;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for Echo using FXML.
 */
public class Main extends Application {

    private Echo echo = new Echo();

    /**
     * Initialises the FXML application.
     * @param stage the primary stage for this application, onto which the application scene can be set. Applications
     *              may create other stages, if needed, but they will not be primary stages.
     */
    @Override
    public void start(Stage stage) {
        try {
            URL layout = Main.class.getResource("/view/MainWindow.fxml");
            if (layout == null) {
                throw new IllegalStateException("The main-window layout is missing.");
            }
            FXMLLoader fxmlLoader = new FXMLLoader(layout);
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setTitle("Echo");
            stage.setMinWidth(417);
            stage.setMinHeight(220);
            fxmlLoader.<MainWindow>getController().setEcho(echo);
            stage.show();
        } catch (IOException | RuntimeException exception) {
            showStartupError();
        }
    }

    private void showStartupError() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Echo could not start");
        alert.setHeaderText("A required application resource could not be loaded.");
        alert.setContentText("Please reinstall Echo.");
        alert.showAndWait();
        Platform.exit();
    }
}
