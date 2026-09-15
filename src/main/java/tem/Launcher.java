package tem;

import javafx.application.Application;

/**
 * Launcher class to work around JavaFX classpath issues in fat JARs.
 */
public class Launcher {
    /**
     * Starts the Tem GUI.
     *
     * @param args command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
