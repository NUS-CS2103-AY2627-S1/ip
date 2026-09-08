package crack.gui;

import javafx.application.Application;

/**
 * Starts the window from a class that does not extend {@link Application},
 * which is the workaround for the JavaFX classpath issue.
 */
public class Launcher {
    /**
     * Starts the chatbot in a window.
     *
     * @param args Command line arguments, which Crack ignores.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
