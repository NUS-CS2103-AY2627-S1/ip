package altair.gui;

import javafx.application.Application;

/**
 * The entry point of the JavaFX application.
 *
 * <p>A separate launcher that does <em>not</em> extend {@link Application} is
 * needed so the app can be started from a plain (non-modular) classpath. If
 * {@code main} lived in a class that extended {@code Application}, the Java
 * launcher would refuse to start it without the JavaFX runtime on the module
 * path. See the SE-EDU JavaFX tutorial, Part 1.</p>
 */
public class Launcher {

    /**
     * Starts the JavaFX runtime, which in turn creates {@link Main}.
     *
     * @param args command-line arguments passed on to JavaFX; not used.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
