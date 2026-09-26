package amadeus;

import javafx.application.Application;

/**
 * Starts the JavaFX window.
 * <p>
 * This class exists only to work around a JavaFX restriction: when the main class of a
 * JAR extends {@link Application}, the JavaFX runtime insists on being loaded from the
 * module path, which a shaded JAR does not use. Launching from a class that does
 * <em>not</em> extend Application sidesteps the check, so {@code java -jar amadeus.jar}
 * works without extra command line flags.
 */
public class Launcher {

    /**
     * Hands control to JavaFX, which then creates and starts {@link Main}.
     *
     * @param args command line arguments; passed through to JavaFX unchanged.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
