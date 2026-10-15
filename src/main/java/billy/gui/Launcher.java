package billy.gui;

import javafx.application.Application;

/**
 * Starts Billy's window.
 *
 * <p>This class does nothing except call {@link Application#launch}, and exists
 * only so that the program's entry point is a class that does <em>not</em>
 * extend {@link Application}. Java refuses to start an {@code Application}
 * subclass directly unless the JavaFX runtime was loaded as a module; inside a
 * plain jar it is on the classpath instead, and the launch fails with a message
 * about missing JavaFX components. Going through an ordinary class sidesteps
 * that check, which then loads JavaFX the normal way.
 */
public class Launcher {

    /**
     * Starts the window.
     *
     * @param args passed on to JavaFX, which understands a few options of its own
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
