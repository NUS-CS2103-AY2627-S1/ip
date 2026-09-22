package stewie.ui.gui;

/**
 * Provides a plain Java entry point for the JAR's bundled JavaFX runtime.
 * Keeping this class separate from Application avoids the Java launcher's module-path-only JavaFX startup.
 */
public final class Launcher {
    private Launcher() {
    }

    /**
     * Launches the desktop interface using the JavaFX classes packaged with the application.
     *
     * @param args Command-line arguments supplied to the application.
     */
    public static void main(String[] args) {
        StewieApplication.main(args);
    }
}
