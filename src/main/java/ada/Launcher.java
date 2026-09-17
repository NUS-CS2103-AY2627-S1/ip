package ada;

import javafx.application.Application;

/**
 * Launches the Ada JavaFX application.
 *
 * This separate launcher class works around JavaFX classpath issues.
 */
public class Launcher {

    /**
     * Starts the Ada JavaFX application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
