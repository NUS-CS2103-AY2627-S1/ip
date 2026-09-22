package yappa.ui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import yappa.Yappa;

/** Starts the JavaFX user interface for Yappa. */
public class Main extends Application {

    private static final String APPLICATION_TITLE = "Yappa - Your No-Nonsense Task Assistant";
    private final Yappa yappa = new Yappa();

    /**
     * Loads and displays the main application window.
     *
     * @param stage Primary JavaFX stage.
     */
    @Override
    public void start(Stage stage) {
        try {
            stage.setTitle(APPLICATION_TITLE);
            stage.setMinHeight(600);
            stage.setMinWidth(600);
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            BorderPane mainPane = fxmlLoader.load();
            Scene scene = new Scene(mainPane);
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setYappa(yappa);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load MainWindow.fxml", e);
        }
    }

}
