package alfred.gui;

import javafx.application.Application;

/**
 * Starts the graphical version of Alfred. A class that extends
 * {@link Application} cannot be the main class of a JAR that bundles JavaFX,
 * so this one does not.
 */
public class Launcher {
    /**
     * Starts the graphical application.
     *
     * @param args command line arguments, handed on to JavaFX unread.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
