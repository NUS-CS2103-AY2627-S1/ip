package lobby;

import javafx.application.Application;
import lobby.gui.Main;

/**
 * Launches the JavaFX application without extending {@link Application} directly.
 */
public class Launcher {

    /**
     * Starts the Lobby graphical user interface.
     *
     * @param args command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
