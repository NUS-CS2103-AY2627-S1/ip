package waffles;

import javafx.application.Application;

/**
 * Starts the JavaFX application through a separate launcher class.
 *
 * <p>The separate entry point avoids the JavaFX classpath issue described in
 * the SE-EDU JavaFX tutorial.</p>
 */
public class Launcher {
    /** Starts the Waffles JavaFX application. */
    public static void main(String... args) {
        if (args.length == 1 && args[0].equals("--cli")) {
            new Waffles().run();
        } else {
            Application.launch(Main.class, args);
        }
    }
}
