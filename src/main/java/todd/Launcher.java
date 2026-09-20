package todd;

import javafx.application.Application;

/** Starts the JavaFX application without extending {@link Application}. */
public class Launcher {
    /** Launches Todd's graphical interface. */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
