package jimbo;

import javafx.application.Application;

/**
 * Real entry point of the packaged application. Launching {@link JimboApp}
 * (a {@code javafx.application.Application}) directly as the JAR's main
 * class fails once packaged, because the JVM can't tell it's a JavaFX app
 * before the JavaFX runtime classes are on the module path. Going through
 * this plain launcher class avoids that classpath issue.
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(JimboApp.class, args);
    }
}
