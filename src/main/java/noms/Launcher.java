package noms;

import javafx.application.Application;
import noms.gui.Main;

/**
 * A thin launcher for the JavaFX GUI.
 *
 * A class that extends {@link javafx.application.Application} cannot be the
 * main class of an executable "fat" JAR (the JavaFX runtime components are not
 * on the module path in that setup). Launching the application from this plain
 * class works around that limitation, so this is the {@code mainClass} the
 * build points at.
 */
public class Launcher {
    /**
     * Starts the JavaFX GUI.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
