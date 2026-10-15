package avon;

import avon.gui.Main;
import javafx.application.Application;

/**
 * Launches Avon's JavaFX application without extending {@link Application}.
 */
public final class Launcher {
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
