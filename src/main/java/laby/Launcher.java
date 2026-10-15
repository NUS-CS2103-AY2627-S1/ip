package laby;

import javafx.application.Application;

/** Launches the JavaFX application and works around JavaFX classpath issues. */
public class Launcher {

    /**
     * Starts the {@link Main} JavaFX application.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
