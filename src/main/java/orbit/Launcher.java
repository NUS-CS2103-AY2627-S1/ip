package orbit;

import javafx.application.Application;
import orbit.gui.Main;

/**
 * Launches JavaFX through a non-Application entry point so the fat JAR can run.
 */
public class Launcher {
    private Launcher() {
    }

    /**
     * Starts the GUI by default, or the retained console interface with --cli.
     *
     * @param args optional --cli argument
     */
    public static void main(String[] args) {
        if (args.length == 1 && args[0].equals("--cli")) {
            Orbit.main(new String[0]);
        } else {
            Application.launch(Main.class, args);
        }
    }
}
