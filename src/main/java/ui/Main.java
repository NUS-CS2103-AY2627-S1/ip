package ui;

import java.io.IOException;

import exception.FriedbergException;
import friedberg.Friedberg;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for Friedberg using FXML.
 */
public class Main extends Application {
    @Override
    public void start(Stage stage) {
        Friedberg friedberg;
        try {
            friedberg = new Friedberg();
        } catch (FriedbergException e) {
            showStartupError(e);
            return;
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane rootPane = fxmlLoader.load();
            Scene scene = new Scene(rootPane);
            stage.setScene(scene);
            stage.setTitle("Friedberg");
            stage.setMinHeight(360);
            stage.setMinWidth(417);
            stage.getIcons().add(new javafx.scene.image.Image(
                    Main.class.getResource("/images/DaDuke.png").toExternalForm()));
            fxmlLoader.<MainWindow>getController().setFriedberg(friedberg);
            stage.show();
        } catch (IOException e) {
            showStartupError(e);
        }
    }

    /**
     * Reports startup failures before an interactive window is shown.
     *
     * @param e failure that prevented startup
     */
    private void showStartupError(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Friedberg startup error");
        alert.setHeaderText("Friedberg could not start");
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
}
