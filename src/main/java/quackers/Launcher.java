package quackers;

import javafx.application.Application;

/**
 * Entry point of the Quackers GUI.
 *
 * <p>This exists purely as a workaround. When the main class extends {@link Application}, the JVM
 * insists that the JavaFX runtime be present as JPMS modules, and otherwise refuses to start with
 * "JavaFX runtime components are missing". Because Gradle puts JavaFX on the plain classpath, we
 * launch from a class that does not extend {@code Application} instead.
 */
public class Launcher {
    /**
     * Starts the JavaFX application.
     *
     * @param args command-line arguments, which are passed on to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
