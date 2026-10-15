package jelly;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/** Starts Jelly's JavaFX application. */
public class Main extends Application {

    private final Jelly jelly = new Jelly();

    /**
     * Loads and displays the FXML-based Jelly window.
     *
     * @param stage the JavaFX application window.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/MainWindow.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(
                    getClass().getResource("/view/jelly.css").toExternalForm());

            MainWindow controller = loader.getController();
            Image userImage = new Image(
                    getClass().getResourceAsStream("/images/hangyodon.png"));
            Image jellyImage = new Image(
                    getClass().getResourceAsStream("/images/jelly.png"));

            controller.setJelly(jelly);
            controller.setStage(stage);
            controller.setImages(userImage, jellyImage);
            controller.showWelcome();

            stage.setTitle("Jelly");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load MainWindow.fxml", e);
        }
    }
}
