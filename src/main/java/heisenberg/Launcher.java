package heisenberg;

import javafx.application.Application;

/** Launches the JavaFX application without inheriting from {@link Application}. */
public class Launcher {
    /**
     * Starts the graphical interface through JavaFX.
     *
     * @param args Command-line arguments forwarded to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
