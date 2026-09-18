package milo.gui;

import javafx.application.Application;

/**
 * A launcher class, separate from {@link Main}, so that {@code Main} can
 * extend {@link Application} without the JVM's classpath-vs-module lookup
 * getting confused about whether the launched class is itself a JavaFX
 * application (a known JavaFX packaging pitfall when the launched class
 * extends Application directly).
 */
public class Launcher {
    /**
     * Starts the GUI.
     *
     * @param args unused; Milo takes no command-line arguments
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
