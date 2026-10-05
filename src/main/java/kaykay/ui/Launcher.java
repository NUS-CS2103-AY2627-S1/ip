package kaykay.ui;

import javafx.application.Application;

/**
 * Launches Kaykay's JavaFX application to work around JavaFX classpath issues.
 */
public final class Launcher {
    private Launcher() {
    }

    /**
     * Launches the JavaFX application.
     *
     * @param args command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
