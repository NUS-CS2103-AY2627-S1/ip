package ff15.gui;

import javafx.application.Application;

/**
 * Starts the JavaFX application from a class that does not itself extend
 * {@link Application}. Launching from a subclass of {@code Application} fails
 * when the JavaFX runtime is on the classpath rather than the module path, so
 * this small class is the one the build points at.
 */
public class Launcher {
    /** Starts FF15's window. */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
