package sevensix;

import javafx.application.Application;

/**
 * Launches the SevenSix JavaFX application.
 */
public final class Launcher {
    /**
     * Prevents construction of this launcher class.
     */
    private Launcher() {
    }

    /**
     * Starts JavaFX with the SevenSix GUI.
     *
     * @param args command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(SevenSixGui.class, args);
    }
}
