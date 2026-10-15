package peter;

import javafx.application.Application;

/**
 * Launches the JavaFX application while avoiding JavaFX classpath issues.
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
