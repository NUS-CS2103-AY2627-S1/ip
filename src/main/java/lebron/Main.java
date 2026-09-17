package lebron;

import java.io.IOException;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * JavaFX entry point for the GUI. Loads the main window and wires it to a
 * {@link Lebron} backend backed by the same {@code ./data/lebron.txt} file
 * the console version ({@link Lebron#main(String[])}) uses.
 */
public class Main extends Application {
    private static final double DEFAULT_WIDTH = 400;
    private static final double DEFAULT_HEIGHT = 600;
    private static final double MIN_WIDTH = 340;
    private static final double MIN_HEIGHT = 220;

    private final Lebron lebron = new Lebron(Paths.get("data", "lebron.txt"));

    /**
     * Builds the scene graph from {@code MainWindow.fxml}, injects the
     * backend into its controller, and shows the window.
     *
     * @param stage the primary stage JavaFX hands to the application
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            Scene scene = new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT);
            scene.getStylesheets().add(Main.class.getResource("/view/lebron.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Lebron");
            stage.setMinWidth(MIN_WIDTH);
            stage.setMinHeight(MIN_HEIGHT);

            MainWindow controller = fxmlLoader.getController();
            controller.setLebron(lebron);
            controller.showGreeting();

            stage.show();
        } catch (IOException e) {
            throw new AssertionError("MainWindow.fxml failed to load", e);
        }
    }

    /**
     * Launches the GUI.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
