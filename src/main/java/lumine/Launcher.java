package lumine;

import javafx.application.Application;

/**
 * Launches the JavaFX application to work around classpath issues.
 */
public class Launcher {

    /**
     * Starts the Lumine JavaFX application.
     *
     * @param args command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
