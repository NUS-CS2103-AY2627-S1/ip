package chione.gui;

import javafx.application.Application;

/**
 * Starts Chione's window.
 *
 * <p>This class exists only to call {@link Application#launch}. Starting an
 * {@code Application} subclass directly makes the Java launcher look for the
 * JavaFX runtime as a set of modules, and complain when it finds them on the
 * plain classpath instead. A class that is not itself an {@code Application} is
 * not checked that way, so launching from here sidesteps the problem.
 */
public class Launcher {
    /** Prevents this class from being instantiated; it only has a way in. */
    private Launcher() {
    }

    /**
     * Starts the window.
     *
     * @param args passed on to JavaFX, which reads its own options from them
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
