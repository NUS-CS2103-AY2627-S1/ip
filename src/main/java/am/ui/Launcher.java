package am.ui;

/**
 * Provides a non-JavaFX entry point for Gradle's application task.
 *
 * <p>Launching a class that directly extends {@code Application} can make the JVM
 * perform a JavaFX runtime preflight before the JavaFX module path is configured.
 * This small indirection lets Gradle start the application normally.</p>
 */
public final class Launcher {
    private Launcher() {
    }

    /**
     * Starts the JavaFX application.
     *
     * @param args command-line arguments passed to JavaFX
     */
    public static void main(String[] args) {
        Main.main(args);
    }
}
