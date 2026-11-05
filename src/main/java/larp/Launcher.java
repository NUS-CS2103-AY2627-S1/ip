package larp;

import javafx.application.Application;

/**
 * Starts the JavaFX application without extending {@link Application}.
 *
 * <p>This separate launcher avoids JavaFX classpath problems when the application is packaged.</p>
 */
public class Launcher {
    private Launcher() {
    }

    /**
     * Launches the Larp graphical interface.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
