package friday;

import javafx.application.Application;

/**
 * Launches the JavaFX application without classpath issues.
 */
public class Launcher {

    /**
     * Launches the Hello World JavaFX application.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
