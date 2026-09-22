package lebron;

import javafx.application.Application;

/**
 * Starts the JavaFX application without making the JAR entry point a JavaFX
 * {@link Application} subclass.
 */
public final class Launcher {
    private Launcher() {
    }

    /**
     * Launches the Lebron GUI.
     *
     * @param args command-line arguments passed to JavaFX
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
