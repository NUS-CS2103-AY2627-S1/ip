package bibi;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * The JavaFX application that puts Bibi in a window.
 *
 * <p>It builds the window from {@code MainWindow.fxml} and hands the controller a
 * {@link Bibi} to talk to. It is launched by {@link Launcher} rather than by its
 * own {@code main}; see that class for why.
 */
public class Main extends Application {
    private static final String MAIN_WINDOW_FXML_PATH = "/view/MainWindow.fxml";
    private static final String STYLESHEET_PATH = "/view/bibi.css";
    private static final double MIN_WINDOW_HEIGHT = 400.0;
    private static final double MIN_WINDOW_WIDTH = 450.0;

    /** The same Bibi the console uses, pointed at the same save file. */
    private final Bibi bibi = new Bibi(Bibi.DEFAULT_SAVE_FILE_PATH);

    /**
     * Creates the application. JavaFX builds it itself when {@link Launcher}
     * starts the GUI, so it must stay public and take no arguments.
     */
    public Main() {
    }

    /**
     * Builds and shows the main window.
     *
     * @param stage the window JavaFX provides for the application
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(findResource(MAIN_WINDOW_FXML_PATH));
            AnchorPane root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(findResource(STYLESHEET_PATH).toExternalForm());
            stage.setScene(scene);

            // The name belongs in the title bar and the taskbar, not only in the
            // greeting, so the window is recognizable when it is not in front.
            stage.setTitle("Bibi");
            stage.getIcons().add(MainWindow.loadImage(MainWindow.BIBI_ICON_PATH));
            stage.setMinHeight(MIN_WINDOW_HEIGHT);
            stage.setMinWidth(MIN_WINDOW_WIDTH);

            // Done after loading, because the controller only exists once the
            // FXML has been read.
            loader.<MainWindow>getController().setBibi(bibi);
            stage.show();
        } catch (IOException exception) {
            // The FXML is packaged inside the application, so failing to read it
            // means a broken build rather than anything the user can act on.
            throw new IllegalStateException("Could not load " + MAIN_WINDOW_FXML_PATH, exception);
        }
    }

    /**
     * Returns the location of a file packaged with the application.
     *
     * <p>A missing file is reported by name here, rather than surfacing later as
     * a bare {@code NullPointerException} from whatever tried to use it.
     *
     * @param resourcePath the file's path inside the resources folder
     * @return the file's location
     */
    private static URL findResource(String resourcePath) {
        URL resource = Main.class.getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Missing bundled resource: " + resourcePath);
        }
        return resource;
    }
}
