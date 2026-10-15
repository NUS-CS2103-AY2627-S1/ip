package alzara.gui;

import java.io.IOException;

import alzara.Alzara;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Entry point for the JavaFX GUI. Loads {@code MainWindow.fxml}, creates the
 * {@link Alzara} instance the GUI talks to, and injects it into the loaded
 * {@link MainWindow} controller.
 */
public class Main extends Application {
    private final Alzara alzara = new Alzara(true);

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();
            Scene scene = new Scene(anchorPane);
            stage.setTitle("Alzara");
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setAlzara(alzara);
            stage.show();
        } catch (IOException exception) {
            throw new RuntimeException("Failed to load MainWindow.fxml", exception);
        }
    }
}
