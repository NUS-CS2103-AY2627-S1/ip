package michael;

import javafx.application.Application;

/**
 * Entry point used by the packaged application.
 *
 * <p>This class deliberately does not extend {@link Application}. The Java
 * launcher treats a main class that extends Application specially and expects
 * JavaFX modules to be installed separately. Starting JavaFX explicitly keeps
 * the single, fat JAR launchable with {@code java -jar}.</p>
 */
public final class Launcher {
    private Launcher() {
        // Utility class; do not instantiate.
    }

    /** Starts the Michael JavaFX application. */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
