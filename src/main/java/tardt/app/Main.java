package tardt.app;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import tardt.TardT;

/**
 * A GUI for TardT using FXML.
 */
public class Main extends Application {
    /** Smallest usable window dimensions, which leave room for the settings, chat, and input areas. */
    private static final int MINIMUM_WINDOW_WIDTH = 280;
    private static final int MINIMUM_WINDOW_HEIGHT = 400;

    private final TardT tardT = new TardT();

    @Override
    public void start(Stage stage) {
        try {
            // Load the FXML before configuring the stage because it supplies the root node and controller.
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane rootPane = fxmlLoader.load();
            Scene scene = new Scene(rootPane);
            stage.setTitle("TardT");
            stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
            stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
            stage.setScene(scene);
            // Give the controller the existing application state after FXML has injected all of its controls.
            fxmlLoader.<MainWindow>getController().setTardT(tardT);
            stage.setOnCloseRequest(event -> tardT.save());
            stage.show();
        } catch (IOException exception) {
            showStartupError();
        }
    }

    /**
     * Shows a concise startup error instead of exposing an implementation stack trace to the user.
     *
     */
    private void showStartupError() {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText("TardT could not start");
        errorAlert.setContentText("The application files could not be loaded. Please restart the application.");
        errorAlert.showAndWait();
    }
}
