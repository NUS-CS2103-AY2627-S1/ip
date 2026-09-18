package bobby.gui;

import java.io.IOException;

import bobby.Bobby;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Sets up Bobby's JavaFX window using FXML.
 */
public class Main extends Application {
    private static final double WINDOW_HEIGHT = 600.0;
    private static final double WINDOW_WIDTH = 400.0;
    private static final double MIN_WINDOW_HEIGHT = 420.0;
    private static final double MIN_WINDOW_WIDTH = 360.0;

    private final Bobby bobby = new Bobby();

    /**
     * Starts the JavaFX GUI.
     *
     * @param stage main application window.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane mainLayout = fxmlLoader.load();
        Scene scene = new Scene(mainLayout);
        scene.getStylesheets().add(getClass().getResource("/view/Main.css").toExternalForm());
        fxmlLoader.<MainWindow>getController().setBobby(bobby);

        stage.setTitle("Bobby");
        stage.setResizable(true);
        stage.setMinHeight(MIN_WINDOW_HEIGHT);
        stage.setMinWidth(MIN_WINDOW_WIDTH);
        stage.setHeight(WINDOW_HEIGHT);
        stage.setWidth(WINDOW_WIDTH);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Launches the JavaFX GUI.
     *
     * @param args command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
