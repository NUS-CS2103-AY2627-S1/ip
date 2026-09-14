package nova;

import javafx.application.Application;
import nova.gui.Main;

/**
 * Starts the GUI.
 *
 * <p>JavaFX refuses to launch an {@code Application} subclass directly from a
 * fat JAR unless the JavaFX modules are on the module path, so the entry point
 * is this ordinary class, which hands over to {@link Main}.
 */
public class Launcher {

    /** Utility class: not meant to be instantiated. */
    private Launcher() {
    }

    /**
     * Starts the chatbot's graphical interface.
     *
     * @param args command line arguments; passed straight to JavaFX
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
