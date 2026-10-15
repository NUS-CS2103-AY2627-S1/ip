package jason.gui;

import javafx.application.Application;

/** Launches Jason's optional JavaFX GUI. */
public final class GuiLauncher {
    private GuiLauncher() {
        // Prevent instantiation of the launcher class.
    }

    /**
     * Starts the JavaFX application.
     *
     * @param args command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(GuiMain.class, args);
    }
}
