package bucket;

import javafx.application.Application;

/**
 * A launcher class to workaround classpath issues.
 *
 * The JavaFX runtime refuses to start when the class holding main extends
 * Application but the JavaFX libraries were loaded from the plain classpath
 * rather than as modules, which is exactly how this project pulls them in.
 * Launching from a class that does not extend Application avoids that check.
 */
public class Launcher {

    /**
     * Starts the JavaFX runtime, which in turn creates and starts Main.
     *
     * @param args Passed through to the JavaFX application.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
