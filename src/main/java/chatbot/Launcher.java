package chatbot;

import javafx.application.Application;

/**
 * Launches the JavaFX application and works around classpath issues.
 */
public class Launcher {

    /**
     * Launches the JavaFX application.
     *
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
