package clearblue.gui;

import javafx.application.Application;

/**
 * Launches {@link Main} through a plain {@code main} method. JavaFX
 * applications launched directly from a class extending {@code Application}
 * can fail to find their dependencies on some classpaths; going through this
 * separate class avoids that problem.
 */
public class Launcher {
    /**
     * Starts the chatbot's GUI.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
