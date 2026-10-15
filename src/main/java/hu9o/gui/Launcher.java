package hu9o.gui;

import javafx.application.Application;

/**
 * Starts the JavaFX GUI from a class that does not itself extend
 * {@link Application}.
 *
 * <p>Launching a subclass of {@link Application} directly fails when the app is
 * run from a shaded ("fat") JAR, because the JavaFX runtime components are then
 * on the classpath rather than the module path. Going through this plain
 * {@code main} avoids that check. Forgetting this class is a common mistake when
 * first adding a GUI (see JavaFX tutorial part 1).
 */
public class Launcher {
    /**
     * Launches the GUI.
     *
     * @param args command-line arguments passed straight through to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
