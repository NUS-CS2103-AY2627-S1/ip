package ducky;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A JavaFX GUI for Ducky, loaded from FXML.
 */
public class Main extends Application {
    private final Ducky ducky = new Ducky();

    /**
     * Loads the main window and shows it.
     *
     * @param stage the primary stage provided by JavaFX.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();
            Scene scene = new Scene(anchorPane);
            scene.getStylesheets().add(Main.class.getResource("/view/main.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Ducky");
            stage.setWidth(600);
            stage.setHeight(700);
            stage.setResizable(true);
            stage.setMinWidth(417);
            stage.setMinHeight(470);
            fxmlLoader.<MainWindow>getController().setDucky(ducky);
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Could not load the main window", e);
        }
    }
}
