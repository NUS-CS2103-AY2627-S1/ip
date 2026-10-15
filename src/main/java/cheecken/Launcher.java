package cheecken;

import javafx.application.Application;

/**
 * Starts JavaFX through a separate entry point, as recommended by the SE-EDU tutorial.
 */
public class Launcher {
    /**
     * Launches the graphical chatbot.
     * @param args command-line arguments (currently unused)
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
