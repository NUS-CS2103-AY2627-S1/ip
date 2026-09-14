package stuart.gui;

import javafx.application.Application;

/**
 * A launcher class to work around a JavaFX classpath issue: launching
 * {@link Main} directly (an {@link Application} subclass) can fail to find
 * JavaFX's runtime classes when run from a plain classpath, whereas
 * launching it indirectly through a separate entry point does not.
 */
public class Launcher {
    /**
     * Launches the Stuart GUI.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
