package am.ui;

import java.io.IOException;

import am.Am;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/** Starts the JavaFX user interface for AM. */
public class Main extends Application {
    private static final double INITIAL_HEIGHT = 600;
    private static final double INITIAL_WIDTH = 400;
    private static final double MINIMUM_HEIGHT = 480;
    private static final double MINIMUM_WIDTH = 360;
    private static final String WINDOW_TITLE = "AM - Task Assistant";

    /**
     * Starts the JavaFX application window using the main FXML view.
     *
     * @param stage primary JavaFX stage
     * @throws IOException if the FXML view cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane mainWindow = fxmlLoader.load();
        MainWindow controller = fxmlLoader.getController();
        assert controller != null : "MainWindow.fxml must provide a controller";
        controller.setAm(new Am());

        Scene scene = new Scene(mainWindow, INITIAL_WIDTH, INITIAL_HEIGHT);
        scene.getStylesheets().add(Main.class.getResource("/css/main.css").toExternalForm());

        stage.setTitle(WINDOW_TITLE);
        stage.setMinWidth(MINIMUM_WIDTH);
        stage.setMinHeight(MINIMUM_HEIGHT);
        stage.setScene(scene);
        stage.show();
    }

    /** Launches AM's JavaFX application. */
    public static void main(String[] args) {
        launch(args);
    }
}
