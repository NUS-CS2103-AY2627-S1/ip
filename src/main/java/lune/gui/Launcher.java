package lune.gui;

import javafx.application.Application;

/**
 * A launcher class to work around classpath issues: launching a class that
 * extends {@link javafx.application.Application} directly can fail to find
 * the JavaFX runtime components on some setups, but launching it indirectly
 * through a separate main class like this one avoids that.
 */
public class Launcher {
    /**
     * Launches the Lune JavaFX GUI ({@link Main}).
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
