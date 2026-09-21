import javafx.application.Application;

/**
 * A launcher class workaround for a JavaFX bug: starting an Application
 * subclass directly (as the JAR's main class) fails when JavaFX isn't on
 * the module path, which is the case here since it's pulled in as a plain
 * dependency. Launching through this ordinary main class instead avoids
 * the problem.
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
