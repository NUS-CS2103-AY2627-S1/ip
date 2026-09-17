package nova;

import javafx.application.Application;
import nova.gui.Main;

/**
 * Starts Nova's JavaFX application without extending {@link Application}.
 */
public class Launcher {
    /**
     * Launches the graphical interface.
     *
     * @param args command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
