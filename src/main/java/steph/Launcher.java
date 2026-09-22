package steph;

import javafx.application.Application;

/**
 * Launches the JavaFX GUI.
 *
 * <p>A separate launcher that does <em>not</em> itself extend
 * {@link javafx.application.Application} is the standard workaround for the
 * "JavaFX runtime components are missing" error that occurs when the main
 * class extends {@code Application} but JavaFX is on the class path rather
 * than the module path.
 */
public class Launcher {

    /**
     * Starts the JavaFX application.
     *
     * @param args Command-line arguments, forwarded to JavaFX (unused).
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
