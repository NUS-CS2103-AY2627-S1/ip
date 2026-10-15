package erina.gui;

import javafx.application.Application;

/**
 * A launcher class to work around a classpath issue.
 *
 * <p>Launching a class that extends {@link Application} directly requires the
 * JavaFX modules to be on the module path. Starting from a class that does not
 * extend it, and calling {@link Application#launch} from there, avoids that
 * requirement.
 */
public class Launcher {
    /**
     * Starts the graphical user interface.
     *
     * @param args passed on to JavaFX
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
