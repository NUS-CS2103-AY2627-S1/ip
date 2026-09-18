package friday;

import javafx.application.Application;

/**
 * Launches the Friday JavaFX application through its application class.
 */
public class Launcher {
    /**
     * Starts JavaFX with {@link Main} as the application class.
     *
     * @param args Command-line arguments forwarded to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
