package bro;

import bro.gui.Main;
import javafx.application.Application;

/** Launches Bro's JavaFX application without triggering JavaFX classpath detection. */
public final class Launcher {
    private Launcher() {
    }

    /** Starts the JavaFX runtime and opens Bro's primary window. */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
