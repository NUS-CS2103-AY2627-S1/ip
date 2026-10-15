package gooble.ui;

import javafx.application.Application;

/** Launches the Gooble JavaFX application through a separate entry point. */
public class Launcher {
    /** Starts the Gooble JavaFX application. */
    public static void main(String[] args) {
        Application.launch(GoobleGui.class, args);
    }
}
