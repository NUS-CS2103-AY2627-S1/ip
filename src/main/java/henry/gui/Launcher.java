package henry.gui;

import javafx.application.Application;

/**
 * Launches Henry's JavaFX application without extending {@link Application} itself.
 */
public class Launcher {
    private Launcher() {
    }

    /**
     * Starts the JavaFX runtime and opens Henry's main window.
     *
     * @param args command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
