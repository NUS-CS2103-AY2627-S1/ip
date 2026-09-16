package tt;

import javafx.application.Application;

/**
 * Launches the JavaFX application without extending {@link Application}.
 */
public class Launcher {
    private Launcher() {
    }

    /**
     * Starts the JavaFX chatbot.
     *
     * @param args command-line arguments supplied to the application.
     */
    public static void main(String[] args) {
        Application.launch(Gui.class, args);
    }
}
