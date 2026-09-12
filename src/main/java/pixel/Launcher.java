package pixel;

import javafx.application.Application;
import pixel.gui.Main;

/**
 * Launches the JavaFX application through a classpath-safe entry point.
 */
public class Launcher {

    private Launcher() {
    }

    /**
     * Starts the Pixel JavaFX application.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String... args) {
        Application.launch(Main.class, args);
    }
}
