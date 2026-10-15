package ace.gui;

import javafx.application.Application;

/**
 * Separate launcher class for the GUI, instead of running {@link Main}
 * directly — this works around a JavaFX classpath issue when launching an
 * Application subclass straight from its own main method.
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
