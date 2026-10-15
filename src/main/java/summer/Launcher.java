package summer;

import javafx.application.Application;

/**
 * Entry point for the GUI. A plain class (not the {@link Application} subclass)
 * launches the JavaFX runtime, to work around a classpath issue that occurs
 * when the {@code Application} subclass is run directly.
 */
public class Launcher {
    /**
     * Launches the JavaFX application.
     *
     * @param args command line arguments, passed through to JavaFX
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
