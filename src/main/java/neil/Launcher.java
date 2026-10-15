package neil;

/**
 * Launches Neil without directly exposing the JavaFX application as the JAR entry point.
 */
public final class Launcher {
    private Launcher() {
        // Prevent construction of this launcher class.
    }

    /**
     * Starts the Neil application.
     *
     * @param args command-line arguments supplied to the application.
     */
    public static void main(String[] args) {
        Main.main(args);
    }
}
