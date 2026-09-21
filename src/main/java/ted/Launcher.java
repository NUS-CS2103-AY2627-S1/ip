package ted;

import javafx.application.Application;

/**
 * Starts the GUI.
 * <p>
 * JavaFX refuses to start when the class holding {@code main} extends
 * {@link Application} and the JavaFX libraries are on the classpath rather than
 * the module path, which is how this project depends on them. Launching from a
 * class that does not itself extend {@link Application} avoids that, so this
 * class exists only to call {@link Application#launch}. Taken from the SE-EDU
 * JavaFX tutorial (https://se-education.org/guides/tutorials/javaFx.html).
 */
public class Launcher {
    /** Launcher is used only through {@link #main(String[])}, so it is never created. */
    private Launcher() {
    }

    /**
     * Starts Ted's GUI.
     *
     * @param args command-line arguments, which Ted does not use.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
