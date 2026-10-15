package zinc.javafx;

import javafx.application.Application;

/**
 * Launches Zinc through a non-JavaFX entry point to avoid classpath issues.
 */
public class ZincLauncher {
    /**
     * Launches the Zinc JavaFX application.
     *
     * @param args Command-line arguments forwarded to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(ZincApplication.class, args);
    }
}
