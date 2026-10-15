package clarry;

import javafx.application.Application;

/**
 * Starts the JavaFX application without extending {@link Application}.
 * This separate entry point avoids JavaFX classpath-launcher issues.
 */
public class Launcher {
    /**
     * Launches Clarry's graphical interface.
     *
     * @param args command-line arguments passed to JavaFX
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
