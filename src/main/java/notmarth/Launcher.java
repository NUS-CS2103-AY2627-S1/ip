package notmarth;

import javafx.application.Application;

/** Starts the NotMarth desktop application. */
public final class Launcher {
    private Launcher() {
    }

    /** Launches the JavaFX application. */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
