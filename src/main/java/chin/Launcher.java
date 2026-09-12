package chin;

import javafx.application.Application;

/**
 * A launcher class to work around classpath issues when launching a JavaFX app
 * that extends {@link javafx.application.Application} directly.
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
