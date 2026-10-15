package tungtung;

import javafx.application.Application;

/** Provides the Java entry point for launching the Tung Tung GUI. */
public class Launcher {

    /**
     * Launches the Tung Tung JavaFX application.
     *
     * @param args command-line arguments forwarded to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(TungTungGui.class, args);
    }
}
