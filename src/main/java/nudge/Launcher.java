package nudge;

import javafx.application.Application;
import nudge.gui.Main;

/**
 * Launches the JavaFX application without extending {@link Application} itself.
 */
public final class Launcher {
    private Launcher() {
    }

    /**
     * Starts the Nudge graphical interface.
     *
     * @param args command-line arguments supplied to the application.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
