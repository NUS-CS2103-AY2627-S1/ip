package peter.gui;

import javafx.application.Application;

/**
 * Starts the graphical interface.
 *
 * <p>This class deliberately does not extend {@link Application}. When the
 * class holding {@code main} extends {@code Application} and JavaFX is on the
 * classpath rather than the module path, the JVM refuses to start with
 * "JavaFX runtime components are missing". Launching from an ordinary class
 * sidesteps that check.
 */
public class Launcher {

    /**
     * Launches the graphical interface.
     *
     * @param args ignored; the data file location is fixed.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
