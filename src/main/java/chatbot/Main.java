package chatbot;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Starts the JavaFX application and displays the main chatbot window.
 */
public class Main extends Application {
    private Yun yun;

    /**
     * Starts the JavaFX application window and loads its FXML layout.
     *
     * @param stage Primary stage provided by JavaFX.
     */
    @Override
    public void start(Stage stage) {
        try {
            yun = new Yun();
        } catch (InvalidInputException e) {
            showStartupError(
                "Invalid task file",
                "The existing taskList.txt contains invalid data.\n\n"
                    + "You should make a backup copy, then rename or remove taskList.txt "
                    + "and restart Yun.\n\n"
                    + "Details: " + e.getMessage());
            return;
        } catch (IllegalStateException e) {
            showStartupError(
                "Unable to access task file",
                "Yo! Please check taskList.txt permissions or its location, then restart Yun.");
            return;
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            fxmlLoader.<MainWindow>getController().setYun(yun);

            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setTitle("Yun");
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load MainWindow.fxml", e);
        }
    }

    /**
     * Displays a user-friendly error encountered before the main window can open.
     *
     * @param header Short description of the startup problem.
     * @param message Instructions for recovering from the startup problem.
     */
    private void showStartupError(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Yun could not start");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
