package pdd.gui;

import javafx.application.Application;

/**
 * Workaround entry point for launching the JavaFX GUI. Runs
 * {@link Main} via {@link Application#launch} instead of extending
 * {@code Application} directly, so this class's own JAR manifest
 * main class never itself extends {@code Application} — avoiding
 * the "missing JavaFX runtime components" error some JavaFX/jar
 * packaging setups throw otherwise.
 */
public class Launcher {
    /** Starts the JavaFX application. */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
