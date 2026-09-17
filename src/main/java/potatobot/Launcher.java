package potatobot;

import javafx.application.Application;
import potatobot.ui.gui.GuiApplication;

/**
 * Launches JavaFX without extending {@link Application}, avoiding classpath launcher issues.
 */
public class Launcher {
    /**
     * Starts the PotatoBot JavaFX application.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(GuiApplication.class, args);
    }
}
