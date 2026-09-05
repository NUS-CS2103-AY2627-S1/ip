package bibi;

import javafx.application.Application;

/**
 * Starts the graphical version of Bibi.
 *
 * <p>This class exists only to hold {@code main}, and deliberately does not
 * extend {@link Application}. JavaFX refuses to start when the class holding
 * {@code main} is itself an {@code Application} and the JavaFX runtime is on the
 * classpath rather than the module path, which is how this project supplies it;
 * the launch then fails with "JavaFX runtime components are missing". Starting
 * from an ordinary class avoids that check entirely.
 */
public class Launcher {
    /**
     * Starts the JavaFX application.
     *
     * @param args command-line arguments, which are handed straight to JavaFX
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
