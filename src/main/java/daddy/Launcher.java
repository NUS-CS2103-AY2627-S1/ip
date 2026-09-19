package daddy;

import daddy.gui.DaddyGui;
import javafx.application.Application;

/**
 * Launches the JavaFX application without triggering JavaFX classpath detection issues.
 */
public class Launcher {
    /** Prevents instantiation because this class only provides the application entry point. */
    private Launcher() {
    }

    /**
     * Starts Daddy's JavaFX application.
     *
     * @param args command-line arguments forwarded to JavaFX
     */
    public static void main(String[] args) {
        Application.launch(DaddyGui.class, args);
    }
}
