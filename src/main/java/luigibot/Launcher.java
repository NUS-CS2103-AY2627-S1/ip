package luigibot;

import javafx.application.Application;
import luigibot.gui.Main;

/**
 * Launches the LuigiBot JavaFX application.
 */
public class Launcher {

    private Launcher() {
    }

    /**
     * Starts the JavaFX application.
     *
     * @param args command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
