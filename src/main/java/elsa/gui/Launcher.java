package elsa.gui;

import javafx.application.Application;

/**
 * Starts the graphical user interface.
 *
 * <p>This class exists only so that the class holding {@code main} is not the
 * one extending {@link Application}. When a JavaFX application is started from
 * a class that extends {@code Application}, the Java runtime looks for the
 * JavaFX modules on the module path and refuses to start when it finds them on
 * the classpath instead, which is where Gradle puts them here. Starting from a
 * class that extends nothing avoids that check, so the application runs from a
 * plain classpath.
 */
public class Launcher {
    /**
     * Prevents this class from being instantiated. Its only member is static,
     * so a Launcher object would hold nothing and do nothing.
     */
    private Launcher() {
    }

    /**
     * Starts the JavaFX application.
     *
     * @param args the command line arguments, passed on to JavaFX unread.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
