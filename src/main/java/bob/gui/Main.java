package bob.gui;

import java.io.IOException;
import java.util.Objects;

import bob.Bob;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Provides an FXML-based graphical user interface for Bob.
 */
public final class Main extends Application {
    private static final double DEFAULT_WIDTH = 440.0;
    private static final double DEFAULT_HEIGHT = 620.0;
    private static final double MINIMUM_WIDTH = 360.0;
    private static final double MINIMUM_HEIGHT = 480.0;

    private final Bob bob = new Bob();

    /**
     * Starts the JavaFX application stage.
     *
     * @param stage the primary stage for this application
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT);
            scene.getStylesheets().add(Objects.requireNonNull(
                    Main.class.getResource("/css/main.css")).toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Bob - Task Foreman");
            stage.setMinWidth(MINIMUM_WIDTH);
            stage.setMinHeight(MINIMUM_HEIGHT);
            stage.setResizable(true);
            fxmlLoader.<MainWindow>getController().setBob(bob);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load MainWindow FXML layout", e);
        }
    }
}
