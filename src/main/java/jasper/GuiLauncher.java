package jasper;

import javafx.application.Application;

/**
 * Serves as a launcher class to workaround JavaFX classpath issues.
 */
public class GuiLauncher {
    /**
     * Launches the graphical user interface application.
     *
     * @param args The command-line arguments.
     */
    public static void main(String[] args) {
        Application.launch(GuiMain.class, args);
    }
}
