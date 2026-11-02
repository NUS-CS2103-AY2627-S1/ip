package megia.ui;

import javafx.application.Application;

/**
 * Starts the Megia JavaFX chatbot in a separate launcher.
 */
public final class GuiLauncher {
    private GuiLauncher() {
    }

    /**
     * Starts the JavaFX application.
     *
     * @param arguments Launch arguments passed to JavaFX.
     */
    public static void main(String[] arguments) {
        Application.launch(MegiaGuiApplication.class, arguments);
    }
}
