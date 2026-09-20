package harold.gui;

import javafx.application.Application;

/**
 * Launches the JavaFX application from a class that does not extend {@link Application}.
 */
public final class Launcher {
    private Launcher() {
    }

    /**
     * Starts Harold's graphical interface.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(HaroldApplication.class, args);
    }
}
