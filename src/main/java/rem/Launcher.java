package rem;

import rem.ui.Main;

/**
 * Launches JavaFX from a regular class so the packaged JAR works on the classpath.
 */
public class Launcher {
    /**
     * Starts the graphical application.
     *
     * @param args Arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Main.launch(Main.class, args);
    }
}
