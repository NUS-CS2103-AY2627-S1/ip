package davidgoggins;

import javafx.application.Application;

/**
 * Starts the GUI without itself extending {@link Application}.
 *
 * <p>A class that extends Application cannot be launched directly from a JAR that puts
 * JavaFX on the classpath rather than the module path: the JavaFX runtime refuses to
 * start and reports missing components. Launching from a class that does not extend
 * Application side-steps that check, which is why this one-line class exists.
 */
public class Launcher {

    /**
     * Starts the JavaFX application.
     *
     * @param args passed straight on to JavaFX; none are read by this app
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
