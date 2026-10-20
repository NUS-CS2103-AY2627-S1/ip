package yapper;

import javafx.application.Application;

/**
 * Launches JavaFX through a plain class so the bundled JAR works on the classpath.
 */
public final class Launcher {
    private Launcher() {
    }

    /**
     * Opens the desktop task companion.
     *
     * @param args Arguments forwarded to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
