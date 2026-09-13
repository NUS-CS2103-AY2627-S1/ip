package alfred;

import java.io.IOException;

import alfred.ui.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Starts Alfred's JavaFX application from its FXML view.
 */
public class Main extends Application {
    private static final double WINDOW_WIDTH = 640.0;
    private static final double WINDOW_HEIGHT = 720.0;

    private final Alfred alfred = Alfred.createGuiAlfred();

    /**
     * Loads and displays the application's primary window.
     *
     * @param stage primary window supplied by JavaFX.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/MainWindow.fxml"));
            AnchorPane mainLayout = fxmlLoader.load();
            Scene scene = new Scene(mainLayout);

            stage.setTitle("Alfred");
            stage.setMinHeight(WINDOW_HEIGHT);
            stage.setMinWidth(WINDOW_WIDTH);
            stage.setResizable(true);
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setAlfred(alfred);
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load Alfred's main window.", e);
        }
    }
}
