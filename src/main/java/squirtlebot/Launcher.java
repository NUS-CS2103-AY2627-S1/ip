package squirtlebot;

import javafx.application.Application;
import squirtlebot.ui.GuiMain;

/**
 *  Launches SquirtleBot while working around classpath issues.
 */
public class Launcher {

    /**
     * Launches SquirtleBot application.
     *
     * @param args command-line arguments to be passed to application.
     */
    public static void main(String[] args) {
        Application.launch(GuiMain.class, args);
    }
}
