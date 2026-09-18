package kenbot;

import javafx.application.Application;

/**
 * A launcher class to workaround classpath issues.
 *
 * <p>Starting {@link Main} directly fails with "JavaFX runtime components are
 * missing" when JavaFX comes from the classpath rather than the module path.
 * A class that does not itself extend {@link Application} sidesteps that check.</p>
 */
public class Launcher {

    /**
     * Creates the launcher. JavaFX requires a public no-argument
     * constructor, so it is written out rather than left implicit.
     */
    public Launcher() {
    }

    /**
     * Starts the JavaFX application.
     *
     * @param args command line arguments, passed through to JavaFX
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
