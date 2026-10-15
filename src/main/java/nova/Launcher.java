package nova;

import javafx.application.Application;
import nova.gui.Main;

/**
 * Launches Nova's JavaFX application without extending {@link Application}.
 */
public class Launcher {
    /**
     * Starts the JavaFX runtime and Nova GUI.
     *
     * @param args command-line arguments passed to JavaFX
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
