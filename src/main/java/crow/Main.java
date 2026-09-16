package crow;

import crow.ui.MainWindow;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Displays the FXML-based JavaFX user interface for Crow.
 */
public class Main extends Application {
    private static final double MINIMUM_WINDOW_HEIGHT = 600.0;
    private static final double MINIMUM_WINDOW_WIDTH = 400.0;

    private final Crow crow = new Crow();

    /**
     * Loads and displays the application's primary window.
     *
     * @param primaryStage Primary stage provided by JavaFX.
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    Main.class.getResource("/view/MainWindow.fxml"));
            BorderPane rootPane = fxmlLoader.load();
            fxmlLoader.<MainWindow>getController().setCrow(crow);

            primaryStage.setScene(new Scene(rootPane));
            primaryStage.setTitle("Crow");
            primaryStage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
            primaryStage.setMinWidth(MINIMUM_WINDOW_WIDTH);
            primaryStage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the main window.", e);
        }
    }
}
