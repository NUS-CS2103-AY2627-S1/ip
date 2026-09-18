package computa;

import javafx.application.Application;

/** Launches the JavaFX application through a separate entry point. */
public class Launcher {
    /** Starts the Computa JavaFX application. */
    public static void main(String[] args) {
        Application.launch(computa.ui.ComputaGui.class, args);
    }
}
