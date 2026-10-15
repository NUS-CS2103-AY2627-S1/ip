package zikiai;

import javafx.application.Application;

import zikiai.ui.Main;

/**
 * Launches JavaFX without making the JAR entry point an Application subclass.
 */
public class Launcher {
    /**
     * Starts the graphical chatbot.
     *
     * @param args arguments forwarded to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
