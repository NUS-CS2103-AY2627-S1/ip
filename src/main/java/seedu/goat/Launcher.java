package seedu.goat;

import javafx.application.Application;

import seedu.goat.ui.Main;

/**
 * Starts the GOAT window.
 * <p>
 * The work is delegated to {@link Main} rather than done by a class that extends
 * {@link Application} itself. Launching an {@code Application} subclass directly makes
 * the JVM insist that the JavaFX modules be on the module path, which fails with
 * "JavaFX runtime components are missing" when the JAR puts them on the classpath
 * instead. A plain class has no such requirement, so this one starts the application
 * on the other's behalf.
 */
public class Launcher {

    /**
     * Opens the chat window.
     *
     * @param args ignored.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
