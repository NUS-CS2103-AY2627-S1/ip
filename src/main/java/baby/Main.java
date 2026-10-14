package baby;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Starts the JavaFX GUI for Baby.
 */
public class Main extends Application {
    /**
     * Starts the main application window.
     *
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            VBox root = fxmlLoader.load();
            Scene scene = new Scene(root);

            stage.setTitle("Baby");
            stage.setMinHeight(520.0);
            stage.setMinWidth(420.0);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
