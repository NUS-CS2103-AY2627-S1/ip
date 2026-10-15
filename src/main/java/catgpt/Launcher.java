package catgpt;

import javafx.application.Application;

/**
 * Launches the CatGPT JavaFX application without extending {@link Application}.
 */
public class Launcher {
    /**
     * Starts the graphical application.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
