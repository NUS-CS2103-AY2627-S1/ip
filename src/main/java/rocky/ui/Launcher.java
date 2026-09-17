package rocky.ui;

import javafx.application.Application;

/**
 * Entry point of the packaged app. Java refuses to start a class that extends
 * {@link Application} directly from a JAR that bundles JavaFX, reporting that
 * "JavaFX runtime components are missing". Starting from this plain class,
 * which then launches {@link Main}, avoids that check.
 */
public class Launcher {
    /**
     * Launches Rocky's JavaFX GUI.
     *
     * @param args command-line arguments, passed on to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
