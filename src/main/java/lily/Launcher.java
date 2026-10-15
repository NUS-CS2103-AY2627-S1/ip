package lily;

import javafx.application.Application;

/** Launches the JavaFX application without Java module-path issues. */
public class Launcher {
    /** Starts Lily's graphical user interface. */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
