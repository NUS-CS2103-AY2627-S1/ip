package blud.ui;

import java.io.IOException;

import blud.Blud;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/** Starts the JavaFX interface for the Blud chatbot. */
public class BludApplication extends Application {
    /** Loads the FXML view and displays the primary application window. */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(BludApplication.class.getResource("/view/MainWindow.fxml"));
            BorderPane root = loader.load();
            loader.<MainWindow>getController().setBlud(new Blud());
            Scene scene = new Scene(root);
            stage.setTitle("Blud Chatbot");
            stage.setMinWidth(560);
            stage.setMinHeight(640);
            stage.setScene(scene);
            stage.show();
        } catch (IOException | RuntimeException exception) {
            showStartupError(stage, exception);
        }
    }

    /** Shows an actionable error when the graphical interface cannot start. */
    private void showStartupError(Stage stage, Exception exception) {
        String reason = exception.getMessage() == null
                ? "An unexpected error occurred."
                : exception.getMessage();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Blud Chatbot");
        alert.setHeaderText("Blud could not start");
        alert.setContentText(reason + "\nPlease check that the task data file is accessible.");
        alert.showAndWait();
        stage.close();
    }
}
