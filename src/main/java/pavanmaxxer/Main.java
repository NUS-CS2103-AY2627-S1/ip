package pavanmaxxer;

import java.io.IOException;
import java.nio.file.Path;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Configures and displays the Pavanmaxxer JavaFX window.
 */
public class Main extends Application {
    private final Pavanmaxxer pavanmaxxer = new Pavanmaxxer(
            Path.of("data", "pavanmaxxer.txt"));

    /**
     * Creates the JavaFX application.
     */
    public Main() {
    }

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane mainLayout = loader.load();
            loader.<MainWindow>getController().setPavanmaxxer(pavanmaxxer);
            stage.setScene(new Scene(mainLayout));
            stage.setTitle("Pavanmaxxer");
            stage.setMinHeight(320);
            stage.setMinWidth(420);
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the GUI.", exception);
        }
    }

    /**
     * Launches the JavaFX application.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
