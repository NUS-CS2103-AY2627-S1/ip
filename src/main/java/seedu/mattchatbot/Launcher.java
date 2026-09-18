package seedu.mattchatbot;

import javafx.application.Application;
import seedu.mattchatbot.gui.Main;

/**
 * Starts the GUI.
 * <p>
 * This class exists only so that the program's main class is not itself a
 * subclass of {@link Application}. When it is, and JavaFX is on the classpath
 * rather than the module path, the runtime refuses to start with a "JavaFX
 * runtime components are missing" error. Launching from a plain class that
 * merely calls {@link Application#launch} sidesteps that, which is what makes
 * the packaged JAR runnable.
 */
public class Launcher {

    /** Not meant to be instantiated: this class only holds the entry point. */
    private Launcher() {
    }

    /**
     * Starts the chatbot's window.
     *
     * @param args command line arguments, which are passed on to JavaFX
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
