package walter;

import javafx.application.Application;
import walter.gui.Main;

/**
 * Starts Walter's JavaFX application without extending {@link Application} directly.
 */
public class Launcher {
    /**
     * Launches the JavaFX application.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
