package tblade.gui;

import javafx.application.Application;

/**
 * A launcher class to work around classpath issues when running the JavaFX application directly.
 */
public class Launcher {
    /**
     * Starts the TBlade GUI application.
     *
     * @param args command-line arguments, which are not used by this application
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
