package botzilla.gui;

import javafx.application.Application;

/**
 * A launcher class used only to start {@link Main}. JavaFX applications
 * launched directly from a class extending {@code Application} can run
 * into classpath issues when packaged as a JAR, so a separate entry point
 * is used to work around this.
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
