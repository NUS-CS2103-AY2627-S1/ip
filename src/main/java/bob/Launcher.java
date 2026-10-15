package bob;

import bob.gui.Main;
import javafx.application.Application;

/**
 * Provides a JavaFX launcher that avoids classpath and module issues.
 */
public final class Launcher {

    private Launcher() {
    }

    /**
     * Launches the JavaFX graphical application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
