package recordbase.ui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import recordbase.Record;

/**
 * JavaFX GUI for the Record application.
 *
 * <p>The class owns the JavaFX application lifecycle. It loads the main FXML layout,
 * configures the primary window, and saves the task list when the window is closed.</p>
 */
public class RecordGui extends Application {
    private static final String WINDOW_ICON_PATH = "/images/RecordAvatar.png";

    /**
     * Creates the JavaFX application instance used by the platform launcher.
     */
    public RecordGui() { }

    /**
     * Creates and displays Record's primary window.
     *
     * <p>The method loads the FXML view and bundled icon, establishes the minimum window
     * dimensions, and registers a close handler that persists the current task list.</p>
     *
     * @param stage the primary stage
     */
    @Override
    public void start(Stage stage) {
        assert stage != null : "Primary stage must not be null";

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(RecordGui.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();

            assert ap != null : "Main window layout must be loaded";

            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setTitle(Record.APP_NAME);
            // Windows scales this square source for both the title bar and taskbar.
            stage.getIcons().add(new Image(RecordGui.class.getResourceAsStream(WINDOW_ICON_PATH)));
            stage.setMinWidth(320);
            stage.setMinHeight(360);
            stage.setOnCloseRequest(event -> {
                Record.saveList("data/listdata.txt");
                Record.goodbye();
            });
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Launches Record directly through its {@code Application} subclass.
     *
     * <p>Packaged distributions normally use {@link Launcher}; this entry point remains
     * useful to IDEs and other environments that already configure JavaFX correctly.</p>
     *
     * @param args command-line arguments forwarded to JavaFX
     */
    public static void main(String[] args) {
        launch(args);
    }
}
