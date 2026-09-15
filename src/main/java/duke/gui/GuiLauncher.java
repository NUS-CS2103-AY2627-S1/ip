package duke.gui;

import javafx.application.Application;

/**
 * A launcher class to workaround classpath issues.
 */
public class GuiLauncher {
    public static void main(String[] args) {
        Application.launch(GuiApp.class, args);
    }
}
