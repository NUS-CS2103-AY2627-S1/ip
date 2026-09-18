package epi.gui;

import javafx.application.Application;

/** Provides the plain Java entry point required to launch JavaFX reliably. */
public class GuiLauncher {
    /**
     * Starts the JavaFX application through a launcher that does not extend {@link Application}.
     *
     * @param args Command-line arguments passed to the JavaFX application.
     */
    public static void main(String[] args) {
        Application.launch(EpiApplication.class, args);
    }
}
