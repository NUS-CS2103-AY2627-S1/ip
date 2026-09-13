package odysseus;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Launches Odysseus's JavaFX conversation window.
 */
public class HelloWorld extends Application {
    private static final double INITIAL_WIDTH = 780;
    private static final double INITIAL_HEIGHT = 640;
    private static final double MINIMUM_WIDTH = 480;
    private static final double MINIMUM_HEIGHT = 440;

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloWorld.class.getResource("/view/MainWindow.fxml"));
            BorderPane root = loader.load();
            Scene scene = new Scene(root, INITIAL_WIDTH, INITIAL_HEIGHT);
            scene.getStylesheets().add(HelloWorld.class.getResource("/view/odysseus.css").toExternalForm());

            MainWindow mainWindow = loader.getController();
            mainWindow.setOdysseus(new Odysseus());
            mainWindow.showWelcome();

            stage.setTitle("Odysseus — Ship's Log");
            stage.setResizable(true);
            stage.setMinWidth(MINIMUM_WIDTH);
            stage.setMinHeight(MINIMUM_HEIGHT);
            stage.setScene(scene);
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Odysseus could not load its conversation window.", exception);
        }
    }

    /**
     * Launches the JavaFX application.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
