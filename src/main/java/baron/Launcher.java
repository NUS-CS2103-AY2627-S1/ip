package baron;

/**
 * Starts Baron without being detected as a JavaFX Application by the JVM's
 * executable JAR launcher.
 */
public final class Launcher {
    private Launcher() {
        // Utility class; do not instantiate.
    }

    /** Delegates startup to the JavaFX application class. */
    public static void main(String[] args) {
        Baron.main(args);
    }
}
