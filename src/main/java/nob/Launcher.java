package nob;

import javafx.application.Application;

/**
 * Launches Nob without extending JavaFX's {@link Application} class directly.
 */
public class Launcher {
    /**
     * Starts the JavaFX application.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(GuiNob.class, args);
    }
}
