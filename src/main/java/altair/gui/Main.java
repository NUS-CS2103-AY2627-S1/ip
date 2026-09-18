package altair.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import altair.Altair;

/**
 * The JavaFX {@link Application} for Altair.
 *
 * <p>{@link #start(Stage)} loads the main window from {@code MainWindow.fxml},
 * hands the window's controller an {@link Altair} instance to talk to, and
 * shows the window.</p>
 */
public class Main extends Application {

    /** The save file the GUI reads from and writes to; shared with the text UI. */
    private static final String STORAGE_PATH = "./data/altair.txt";

    /** The task manager that produces the replies shown in the window. */
    private final Altair altair = new Altair(STORAGE_PATH);

    /**
     * Builds and shows the main window.
     *
     * @param stage the primary stage provided by JavaFX.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Main.class.getResource("/view/altair.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Altair");
            stage.setMinWidth(400.0);
            stage.setMinHeight(600.0);

            MainWindow controller = fxmlLoader.getController();
            controller.setAltair(altair);

            stage.show();
        } catch (IOException exception) {
            // The FXML is bundled with the app, so this only happens if the
            // build is broken; there is nothing the user can do to recover.
            throw new IllegalStateException("Could not load the main window.", exception);
        }
    }
}
