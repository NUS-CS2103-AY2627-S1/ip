package slotbot.gui;

import javafx.application.Application;

/**
 * Launches JavaFX from a separate class for classpath-based JAR execution.
 */
public class Launcher {
    /**
     * Starts the graphical interface.
     *
     * @param args Application arguments.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
