package eve.gui;

import javafx.application.Application;

/**
 * A separate entry point from {@link MainApp}, needed to work around a
 * classpath issue when a JavaFX application is launched from a fat/shadow
 * JAR (see the JavaFX tutorial, Part 1): JavaFX inspects the class that
 * declares {@code main} to decide whether it's itself an
 * {@link Application}, and gets confused if that class is the
 * {@code Application} subclass itself.
 */
public class Launcher {
    /**
     * Launches the GUI.
     *
     * @param args unused.
     */
    public static void main(String[] args) {
        Application.launch(MainApp.class, args);
    }
}
