package meowmeow.gui;

import javafx.application.Application;

/**
 * A launcher class to work around a JavaFX classpath issue. When JavaFX is
 * on the classpath rather than the module path, {@link Application}
 * subclasses cannot be the {@code main} class directly, so this plain class
 * launches {@link Main} instead.
 */
public class Launcher {

    /**
     * Starts the JavaFX application.
     *
     * @param args command-line arguments, forwarded to JavaFX (unused).
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
