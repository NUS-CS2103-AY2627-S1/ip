package verity.gui;

import javafx.application.Application;

/**
 * A launcher class to work around a JavaFX classpath issue, per the JavaFX tutorial.
 */
public class Launcher {

    /**
     * Launches the GUI.
     *
     * @param args Command-line arguments; not used.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
