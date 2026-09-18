package littledaisy;

import javafx.application.Application;

/** Starts the JavaFX application without extending a JavaFX class itself. */
public final class Launcher {
    private Launcher() {
    }

    /** Launches littleDaisy's graphical interface. */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
