package duncan.gui;

import javafx.application.Application;

/**
 * A launcher class to workaround classpath issues.
 *
 * <p>{@link Main} extends {@link Application}, and launching such a class
 * directly requires the JavaFX modules to be on the module path. Starting
 * from a class that does not extend {@code Application} avoids that, so the
 * program can be run from a plain classpath, e.g. out of the shaded JAR.
 */
public class Launcher {
    /** Entry point for the GUI: hands over to the JavaFX application. */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
