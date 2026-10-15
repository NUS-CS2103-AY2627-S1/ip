package yapbot.gui;

import javafx.application.Application;

/**
 * A plain {@code main()} that hands off to {@link Main} via
 * {@link Application#launch}. JavaFX apps launched from a shadow/fat jar
 * need a launcher class that does not itself extend {@link Application},
 * otherwise the runtime cannot find the bundled JavaFX modules and fails
 * with a "missing JavaFX runtime components" error.
 */
public class Launcher {

    /**
     * Launches the YapBot GUI.
     *
     * @param args command-line arguments, passed through to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
