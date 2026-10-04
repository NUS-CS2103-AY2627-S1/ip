package happy;
import javafx.application.Application;
/**
 * Main launcher class to bypass JavaFX classpath check issues.
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
