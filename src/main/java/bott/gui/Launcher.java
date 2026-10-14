package bott.gui;

import javafx.application.Application;

/**
 * Launches Bott's JavaFX GUI.
 *
 * <p>A launcher that does not itself extend {@link Application} is needed
 * so the GUI can be started from a plain {@code java -jar} invocation:
 * starting an {@code Application} subclass directly requires the JavaFX
 * runtime components to be on the module path, which a shaded JAR does
 * not set up.
 */
public class Launcher {

    /**
     * Starts Bott's GUI.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
