package nori;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import nori.ui.MainWindow;

/**
 * Sets up the main JavaFX window for Nori.
 */
public class Main extends Application {
    private final Nori nori = new Nori();

    /**
     * Loads and displays Nori's main window.
     *
     * @param stage Primary JavaFX stage.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane mainLayout = fxmlLoader.load();
            Scene scene = new Scene(mainLayout);

            stage.setTitle("Nori");
            stage.setMinHeight(400);
            stage.setMinWidth(420);
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setNori(nori);
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load Nori's main window.", e);
        }
    }
}
