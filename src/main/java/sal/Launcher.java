package sal;

import javafx.application.Application;

/**
 * A launcher class to workaround classpath issues with JavaFX.
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
