package nerrad;

import javafx.application.Application;

/**
 * Launches Nerrad's JavaFX application without JavaFX classpath conflicts.
 */
public final class Launcher {
    private Launcher() {
    }

    /**
     * Launches Nerrad's graphical user interface.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
