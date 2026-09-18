package habpyduck.ui;

import java.io.IOException;

import habpyduck.HabpyDuck;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Displays the main JavaFX window for HabpyDuck.
 */
public class Main extends Application {
    private static final int WINDOW_WIDTH = 400;
    private static final int WINDOW_HEIGHT = 600;

    /**
     * Launches the JavaFX application.
     *
     * @param args Command line arguments supplied by the Java launcher.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Creates the first JavaFX scene shown to the user.
     *
     * @param stage the main application window.
     * @throws IOException if the FXML file cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        VBox root = fxmlLoader.load();
        stage.setMinHeight(220);
        stage.setMinWidth(500);
        MainWindow mainWindow = fxmlLoader.getController();
        mainWindow.setHabpyDuck(new HabpyDuck());

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setTitle("HabpyDuck");
        stage.setScene(scene);
        stage.show();
    }
}
