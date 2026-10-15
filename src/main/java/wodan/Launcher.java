package wodan;

import javafx.application.Application;
import wodan.gui.Main;

/**
 * Workaround entry point so JavaFX starts with a correct classpath.
 */
public class Launcher {
    /**
     * Launches the graphical UI.
     *
     * @param args Unused.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
