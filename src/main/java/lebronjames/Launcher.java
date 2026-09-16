package lebronjames;

import javafx.application.Application;
import lebronjames.gui.Main;

/**
 * Starts the graphical version of the chatbot.
 *
 * <p>This class exists only to call {@link Application#launch}, and deliberately
 * does not extend {@link Application} itself. When JavaFX is on the classpath
 * rather than the module path, as it is inside the packaged JAR, the launcher
 * refuses to start a class that extends {@code Application}. Putting the entry
 * point in a plain class that is not a JavaFX application side-steps that check,
 * so the same JAR runs whether or not JavaFX is available as a module.
 */
public class Launcher {
    /**
     * Starts the application window.
     *
     * @param args Command line arguments, which the chatbot does not use.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
