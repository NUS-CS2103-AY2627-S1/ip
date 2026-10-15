package trybot.gui;

import javafx.application.Application;

/** Starts the JavaFX application in a separate launcher class. */
public class Launcher {
    /**
     * Launches the TryBot JavaFX application.
     *
     * @param args command-line arguments.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
