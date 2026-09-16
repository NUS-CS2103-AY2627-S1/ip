package discitrack.gui;

import java.io.IOException;

import discitrack.DisciTrack;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Starts and displays the main JavaFX window for DisciTrack.
 */
public class Main extends Application {
    /**
     * Launches the JavaFX application.
     *
     * @param args command-line arguments supplied to the application.
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            Parent mainLayout = fxmlLoader.load();
            Scene scene = new Scene(mainLayout);

            stage.setScene(scene);
            stage.setTitle("DisciTrack");
            stage.setMinHeight(580);
            stage.setMinWidth(660);

            MainWindow mainWindow = fxmlLoader.getController();
            mainWindow.setDisciTrack(new DisciTrack());
            stage.sizeToScene();
            Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
            stage.setHeight(Math.min(stage.getHeight(), visualBounds.getHeight() * 0.9));
            stage.setWidth(Math.min(stage.getWidth(), visualBounds.getWidth() * 0.9));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
