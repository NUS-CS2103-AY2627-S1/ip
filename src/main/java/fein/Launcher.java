package fein;

import javafx.application.Application;

/** Launches Fein's JavaFX application through a separate entry point. */
public class Launcher {
    /** Starts the JavaFX runtime and opens Fein's main window. */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
