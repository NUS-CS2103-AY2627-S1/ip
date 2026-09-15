package tianyi;

import javafx.application.Application;

/**
 * Launches the Tianyi JavaFX application.
 */
public class Launcher {
    /**
     * Prevents instantiation of this utility class.
     */
    private Launcher() {
    }

    /**
     * Starts the Tianyi graphical user interface.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
