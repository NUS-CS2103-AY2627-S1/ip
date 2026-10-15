package suki;

import javafx.application.Application;

/**
 * Starts the GUI.
 *
 * <p>This exists only as a workaround: when JavaFX is on the classpath rather
 * than the module path, the JavaFX runtime refuses to start if the class
 * holding {@code main} is itself an {@link Application}. Launching from a
 * separate class that is not an {@code Application} avoids that.
 */
public class Launcher {
    /**
     * Starts Suki's GUI.
     *
     * @param args command line arguments, which are passed on to JavaFX
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
