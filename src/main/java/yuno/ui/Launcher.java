package yuno.ui;

import javafx.application.Application;

/**
 * Launches Yuno's JavaFX application.
 */
public final class Launcher {

    /**
     * Prevents the launcher class from being instantiated.
     */
    private Launcher() {
    }

    /**
     * Starts the JavaFX application.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
