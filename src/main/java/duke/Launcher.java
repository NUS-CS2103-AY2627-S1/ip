package duke;

import javafx.application.Application;

/** Separate JavaFX launcher that avoids module-system startup issues. */
public class Launcher {
    /** Launches the Furina JavaFX application. */
    public static void main(String[] args) {
        Application.launch(FurinaGui.class, args);
    }
}
