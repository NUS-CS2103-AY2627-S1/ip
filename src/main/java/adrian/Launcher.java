package adrian;

import javafx.application.Application;

/**
 * Launches the Adrian JavaFX application.
 */
public class Launcher {

    private Launcher() {
    }

    /**
     * Starts JavaFX using the Adrian graphical interface.
     *
     * @param args command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
