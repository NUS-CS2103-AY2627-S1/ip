package monkey;

import javafx.application.Application;

/** Launches the JavaFX application without extending Application itself. */
public class Launcher {
    /** Starts the Monkey graphical user interface. */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
