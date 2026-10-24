package shannon.gui;

import javafx.application.Application;

/**
 * Starts the GUI.
 * <p>
 * This class exists only to be the program's entry point, and deliberately does <em>not</em>
 * extend {@link Application}. When the class holding {@code main} is itself an
 * {@code Application}, Java insists that JavaFX be loaded as a module and refuses to start with
 * "JavaFX runtime components are missing". Launching from an ordinary class side-steps that, so
 * the JAR can be run with a plain {@code java -jar}.
 */
public class Launcher {

    /**
     * Hands control to JavaFX, which then creates a {@link Main} and calls its
     * {@code start} method.
     *
     * @param args command line arguments, passed on to JavaFX but not used by this program.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
