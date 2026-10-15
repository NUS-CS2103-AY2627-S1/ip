package rocky.gui;

import javafx.application.Application;

/** Starts the JavaFX application through a plain Java entry point. */
public class Launcher {
    /** Launches the Rocky JavaFX application.
     *
     * @param args command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(RockyGui.class, args);
    }
}
