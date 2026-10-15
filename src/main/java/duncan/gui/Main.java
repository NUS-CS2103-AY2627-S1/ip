package duncan.gui;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import duncan.Duncan;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * A GUI for Duncan using FXML. Builds the window from
 * {@code /view/MainWindow.fxml} and hands the controller the {@link Duncan}
 * instance that does the actual work.
 */
public class Main extends Application {
    /** The smallest window the logo still fits in, so the window may not be dragged narrower. */
    private static final double MIN_WINDOW_WIDTH = 460.0;
    private static final double MIN_WINDOW_HEIGHT = 420.0;

    private static final String FONT_PATH = "/fonts/JetBrainsMono-Regular.ttf";

    /** Size the font is loaded at; the style sheet sets the sizes actually shown. */
    private static final double FONT_LOAD_SIZE = 12.0;

    private static final String STYLESHEET_PATH = "/css/duncan.css";
    private static final String ICON_PATH = "/images/icon.png";
    private static final String WINDOW_TITLE = "Duncan";

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private final Duncan duncan = new Duncan();

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            BorderPane root = fxmlLoader.load();

            // The font has to be registered before the style sheet asks for it by name.
            loadBundledFont();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Main.class.getResource(STYLESHEET_PATH).toExternalForm());

            stage.setScene(scene);
            stage.setTitle(WINDOW_TITLE);
            stage.setMinWidth(MIN_WINDOW_WIDTH);
            stage.setMinHeight(MIN_WINDOW_HEIGHT);
            stage.getIcons().add(new Image(Main.class.getResourceAsStream(ICON_PATH)));
            fxmlLoader.<MainWindow>getController().setDuncan(duncan);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Registers the bundled monospace font with JavaFX, reading it from inside
     * the JAR so it works wherever the program is run from. If it cannot be
     * loaded the window still opens, using the platform's own monospace font,
     * but the failure is logged rather than left silent.
     */
    private void loadBundledFont() {
        try (InputStream fontStream = Main.class.getResourceAsStream(FONT_PATH)) {
            Font font = (fontStream == null) ? null : Font.loadFont(fontStream, FONT_LOAD_SIZE);
            if (font == null) {
                LOGGER.severe("Could not load the bundled font " + FONT_PATH
                        + "; falling back to the platform's default font.");
                return;
            }
            LOGGER.info("Loaded font family: " + font.getFamily());
        } catch (IOException e) {
            LOGGER.severe("Could not read the bundled font " + FONT_PATH + ": " + e.getMessage());
        }
    }
}
