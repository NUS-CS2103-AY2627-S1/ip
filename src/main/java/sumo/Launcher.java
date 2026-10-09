package sumo;

import javafx.application.Application;

import sumo.gui.Main;

/** Launches the JavaFX application without extending {@link Application}. */
public class Launcher {
    /** Creates an application launcher. */
    public Launcher() {
    }

    /**
     * Starts Sumo's graphical interface.
     *
     * @param args command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
