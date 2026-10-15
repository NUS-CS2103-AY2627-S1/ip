package bern;

import java.io.IOException;

import bern.logic.Controller;
import bern.ui.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * JavaFX application entry point for Bern, loading the main window from FXML.
 */
public class Bern extends Application {
    private final Controller control = new Controller();
    private Image bernImage = new Image(this.getClass().getResourceAsStream("/images/DaBern.png"));

    /**
     * Creates the application instance that JavaFX initializes before calling {@link #start(Stage)}.
     */
    public Bern() {
    }

    /**
     * Loads the main window, connects its controller, and displays startup messages before showing the stage.
     * Prints a stack trace if loading the FXML raises an {@link IOException}.
     *
     * @param stage The primary stage supplied by JavaFX.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Bern.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.getIcons().add(bernImage);
            stage.setTitle("Bern Tokens Task Manager");
            stage.setMinHeight(220);
            stage.setMinWidth(417);

            assert fxmlLoader.getController() != null;
            fxmlLoader.<MainWindow>getController().setController(control); // Inject the Controller instance
            fxmlLoader.<MainWindow>getController().displayStartupMessages();

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
