package duke;

import javafx.application.Application;

/** Launches JavaFX from a class that does not extend {@link Application}. */
public final class Launcher {

    private Launcher() {
    }

    /**
     * Starts the Hermes graphical interface.
     *
     * @param args command-line arguments passed to JavaFX
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
