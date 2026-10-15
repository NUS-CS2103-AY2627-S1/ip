package potato;

import javafx.application.Application;

/**
 * A launcher class to workaround JavaFX entry point limitations.
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}