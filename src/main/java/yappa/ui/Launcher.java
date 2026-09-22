package yappa.ui;

import javafx.application.Application;

/** Launches the JavaFX application through a classpath-safe entry point. */
public class Launcher {
    /**
     * Launches the main JavaFX application.
     *
     * @param args Application arguments.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }

}
