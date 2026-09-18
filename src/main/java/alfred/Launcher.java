package alfred;

import alfred.gui.Main;
import javafx.application.Application;

/**
 * Workaround entry point so JavaFX can load its classes from the classpath.
 */
public class Launcher {
    /**
     * Launches the JavaFX GUI.
     *
     * @param args Command-line arguments forwarded to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
