package eli;

import javafx.application.Application;

/** Launches the JavaFX application without extending {@link Application}. */
public class Launcher {
    /** Starts Eli's graphical interface. */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
