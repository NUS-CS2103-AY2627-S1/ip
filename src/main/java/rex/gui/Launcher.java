package rex.gui;

import javafx.application.Application;

/**
 * Starts the graphical version of Rex.
 *
 * This exists only to keep the entry point out of Main. Launching a class that
 * extends Application directly fails when the JavaFX classes come from the
 * classpath rather than the module path, which is how the shaded JAR ships
 * them, so the JAR names this class instead and it starts Main in turn.
 */
public class Launcher {
    /**
     * Starts the application.
     *
     * @param args passed on to JavaFX, which reads its own options from them.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
