package janet;

import javafx.application.Application;

/**
 * Launches Janet's JavaFX application without extending {@link Application}.
 */
public class Launcher {
    /**
     * Starts Janet's graphical interface.
     *
     * @param args command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
