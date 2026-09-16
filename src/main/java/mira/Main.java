package mira;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import mira.exception.MiraException;
import mira.ui.MainWindow;

/**
 * Starts and configures Mira's JavaFX user interface.
 */
public class Main extends Application {
    private static final String MAIN_WINDOW_FXML = "/view/MainWindow.fxml";
    private static final String STYLESHEET = "/view/Mira.css";

    /**
     * Loads Mira's main window and shows it on the supplied stage.
     *
     * @param stage Primary JavaFX window.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(MAIN_WINDOW_FXML));
            AnchorPane root = fxmlLoader.load();
            MainWindow controller = fxmlLoader.getController();
            controller.setMira(new Mira());

            Scene scene = new Scene(root);
            URL stylesheet = getClass().getResource(STYLESHEET);
            if (stylesheet != null) {
                scene.getStylesheets().add(stylesheet.toExternalForm());
            }

            stage.setTitle("Mira");
            stage.setMinWidth(420.0);
            stage.setMinHeight(520.0);
            stage.setScene(scene);
            stage.show();
        } catch (IOException | MiraException exception) {
            showStartupError(exception.getMessage());
        }
    }

    /**
     * Shows an error if Mira cannot load its data or interface.
     *
     * @param details Technical error details, if available.
     */
    private void showStartupError(String details) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Mira could not start");
        alert.setHeaderText("The application could not be opened.");
        alert.setContentText(details == null ? "An unexpected startup error occurred." : details);
        alert.showAndWait();
    }
}
