package dennis.gui;

import javafx.application.Application;

/**
 * Starts the GUI from a class that does not itself extend {@link Application}.
 *
 * <p>When the class declaring {@code main} extends {@code Application},
 * launching from a plain classpath (no JavaFX modules on the module path)
 * fails with "JavaFX runtime components are missing". Delegating to
 * {@link Application#launch(Class, String...)} from this separate, non-{@code
 * Application} class sidesteps that problem.</p>
 */
public final class Launcher {
    /** Utility class; not meant to be instantiated. */
    private Launcher() {
    }

    /**
     * Launches the JavaFX application.
     *
     * @param args passed straight through to JavaFX
     */
    public static void main(String[] args) {
        Application.launch(MainApp.class, args);
    }
}
