package bo.gui;

import java.io.IOException;

import bo.Bo;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/** The JavaFX entry point for the Bo chatbot. */
public final class Main extends Application {
    /** The minimum width of the main window. */
    private static final double MIN_WINDOW_WIDTH = 520.0;

    /** The minimum height of the main window. */
    private static final double MIN_WINDOW_HEIGHT = 640.0;

    /** The application logic shared with the command-line interface. */
    private final Bo bo = new Bo();

    /** Creates the JavaFX application and its application-logic instance. */
    public Main() {
    }

    /**
     * Loads the FXML view, injects the application logic, and shows the window.
     *
     * @param stage the primary JavaFX stage.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            MainWindow mainWindow = fxmlLoader.getController();
            mainWindow.setBo(bo);

            Scene scene = new Scene(root);
            stage.setTitle("Bo");
            stage.setMinWidth(MIN_WINDOW_WIDTH);
            stage.setMinHeight(MIN_WINDOW_HEIGHT);
            stage.setScene(scene);
            stage.show();
        } catch (IOException | RuntimeException exception) {
            throw new IllegalStateException("Unable to load the Bo GUI.", exception);
        }
    }
}
