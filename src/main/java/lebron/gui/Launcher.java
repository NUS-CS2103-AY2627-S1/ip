package lebron.gui;

import javafx.application.Application;

/**
 * A plain (non-Application) entry point that just calls {@link Application#launch}.
 * Needed because launching a JavaFX {@code Application} subclass's own {@code main()}
 * directly from a shaded/fat jar can fail JavaFX's module-path checks; going through
 * a separate launcher class avoids that.
 */
public class Launcher {
    /**
     * Starts the JavaFX GUI.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
