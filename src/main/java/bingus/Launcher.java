// Adapted from the SE-EDU JavaFX Tutorial, Part 4:
// https://se-education.org/guides/tutorials/javaFxPart4.html

package bingus;

import javafx.application.Application;

/**
 * Launches the JavaFX application to work around classpath issues.
 */
public class Launcher {
    /**
     * Launches the Bingus JavaFX application.
     *
     * @param args command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
