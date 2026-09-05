package bibi;

import java.io.IOException;

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
    private static final String MAIN_WINDOW_FXML = "/view/MainWindow.fxml";

    /** The same Bibi the console uses, pointed at the same save file. */
    private final Bibi bibi = new Bibi(Bibi.DEFAULT_SAVE_FILE_PATH);

    /**
     * Builds and shows the main window.
     *
     * @param stage the window JavaFX provides for the application
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(MAIN_WINDOW_FXML));
            AnchorPane root = loader.load();

            stage.setScene(new Scene(root));
            stage.setTitle("Bibi");
            stage.setMinHeight(400.0);
            stage.setMinWidth(450.0);

            // Done after loading, because the controller only exists once the
            // FXML has been read.
            loader.<MainWindow>getController().setBibi(bibi);
            stage.show();
        } catch (IOException exception) {
            // The FXML is packaged inside the application, so failing to read it
            // means a broken build rather than anything the user can act on.
            throw new IllegalStateException("Could not load " + MAIN_WINDOW_FXML, exception);
        }
    }
}
