package mouse;

import javafx.application.Application;
import mouse.gui.Main;

/**
 * Workaround entry point for JavaFX classpath issues in a fat JAR.
 */
public class Launcher {
    /**
     * Starts the JavaFX GUI.
     *
     * @param args Command-line arguments forwarded to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
