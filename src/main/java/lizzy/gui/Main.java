package lizzy.gui;

import java.io.IOException;
import java.io.InputStream;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lizzy.Lizzy;

/**
 * Configures and displays Lizzy's JavaFX window.
 */
public class Main extends Application {
    private static final double DEFAULT_FONT_SIZE = 14.0;
    private static final double MINIMUM_WINDOW_HEIGHT = 480.0;
    private static final double MINIMUM_WINDOW_WIDTH = 400.0;
    private static final String[] FONT_RESOURCES = {
        "/fonts/SourceSerif4-Regular.ttf",
        "/fonts/SourceSerif4-Italic.ttf",
        "/fonts/SourceSerif4-Semibold.ttf"
    };
    private static final String MAIN_WINDOW_FXML = "/view/MainWindow.fxml";

    /**
     * Builds the main window and connects it to the chatbot.
     *
     * @param stage the primary JavaFX stage
     * @throws IOException if the FXML resource cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        registerApplicationFonts();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(MAIN_WINDOW_FXML));
        Parent root = fxmlLoader.load();
        MainWindow mainWindow = fxmlLoader.getController();
        mainWindow.setLizzy(new Lizzy());

        stage.setTitle("Lizzy");
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        double availableScreenWidth = visualBounds.getWidth();
        double availableScreenHeight = visualBounds.getHeight();
        stage.setMinWidth(Math.min(MINIMUM_WINDOW_WIDTH, availableScreenWidth));
        stage.setMinHeight(Math.min(MINIMUM_WINDOW_HEIGHT, availableScreenHeight));
        stage.setMaxWidth(availableScreenWidth);
        stage.setMaxHeight(availableScreenHeight);
        stage.setResizable(true);
        stage.setScene(new Scene(root));
        stage.show();
        fitWithinVisualBounds(stage, visualBounds);
        mainWindow.focusInput();
    }

    /**
     * Keeps the complete initial window inside the usable area of its screen.
     *
     * @param stage the window to resize and position
     * @param visualBounds the screen area excluding system bars and docks
     */
    private static void fitWithinVisualBounds(Stage stage, Rectangle2D visualBounds) {
        double fittedWidth = Math.min(stage.getWidth(), visualBounds.getWidth());
        double fittedHeight = Math.min(stage.getHeight(), visualBounds.getHeight());
        stage.setWidth(fittedWidth);
        stage.setHeight(fittedHeight);
        stage.setX(visualBounds.getMinX() + (visualBounds.getWidth() - fittedWidth) / 2);
        stage.setY(visualBounds.getMinY() + (visualBounds.getHeight() - fittedHeight) / 2);
    }

    /**
     * Registers the bundled font faces before the scene's stylesheet is applied.
     *
     * @throws IOException if a font resource is missing or cannot be loaded
     */
    private static void registerApplicationFonts() throws IOException {
        for (String fontResource : FONT_RESOURCES) {
            try (InputStream fontStream = Main.class.getResourceAsStream(fontResource)) {
                if (fontStream == null || Font.loadFont(fontStream, DEFAULT_FONT_SIZE) == null) {
                    throw new IOException("Unable to load application font: " + fontResource);
                }
            }
        }
    }
}
