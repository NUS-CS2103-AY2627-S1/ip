package bobby;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/** A GUI for Bobby using FXML. */
public class Main extends Application {

    /** Chatbot instance used to generate responses for GUI input. */
    private final Bobby bobby = new Bobby();

    /**
     * Loads the main FXML view and displays the JavaFX stage.
     *
     * @param stage primary JavaFX stage
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setBobby(bobby);
            stage.setMinHeight(320);
            stage.setMinWidth(360);
            stage.setResizable(true);
            stage.setTitle("Bobby's Task Playground");
            stage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/Bobby.png")));
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the main window layout.", exception);
        }
    }
}
