package alzara.gui;

import javafx.application.Application;

/**
 * Launches the JavaFX GUI via {@link Main}, going through {@link Application#launch}
 * with an explicit class reference rather than calling {@code main} directly - this
 * works around classpath issues that JavaFX's module system otherwise runs into when
 * launched from a fat/shadow JAR.
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
