package ramly.gui;

import javafx.application.Application;

/** Launches the JavaFX application to work around the JavaFX classpath issue. */
public class Launcher {
    /** Starts the Ramly JavaFX application. */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
