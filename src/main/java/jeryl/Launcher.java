package jeryl;

import javafx.application.Application;

/**
 * A launcher class to work around a JavaFX classpath issue that occurs
 * when the Application subclass (Main) is used directly as the entry
 * point of a fat/shadow JAR.
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
