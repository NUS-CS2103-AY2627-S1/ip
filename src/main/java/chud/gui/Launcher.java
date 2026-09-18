package chud.gui;

import javafx.application.Application;

/**
 * A separate entry point that hands off to {@link Main} via {@link Application#launch}. JavaFX
 * refuses to launch an {@code Application} subclass that is itself the JAR/classpath main class,
 * so this indirection is required.
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
