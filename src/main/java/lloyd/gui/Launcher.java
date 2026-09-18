package lloyd.gui;

import javafx.application.Application;

/**
 * Starts the JavaFX runtime without requiring the launcher itself to extend
 * {@link Application}.
 */
public final class Launcher {

    private Launcher() {
        // Prevents instantiation of this entry-point class.
    }

    /**
     * Launches the Lloyd graphical application.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
