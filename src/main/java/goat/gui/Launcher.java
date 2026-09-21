package goat.gui;

import javafx.application.Application;

/**
 * Starts the graphical version of Goat.
 *
 * This class exists only to call {@link Main}, and it is not itself a JavaFX
 * {@code Application}. That indirection is deliberate and is the usual cause of
 * "JavaFX runtime components are missing" when it is left out.
 *
 * When the JVM is asked to run a class that extends {@code Application}
 * directly, it checks that the JavaFX modules were loaded as modules and
 * refuses to start if they were not. A fat JAR puts them on the classpath
 * instead, so that check fails. Launching from a plain class sidesteps the
 * check: by the time {@code Application.launch} runs, the classes are already
 * loaded and the JAR works anywhere.
 */
public class Launcher {

    /**
     * Starts the application.
     *
     * @param args passed straight through to JavaFX
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
