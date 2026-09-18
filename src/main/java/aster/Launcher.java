package aster;

import aster.gui.Main;
import javafx.application.Application;

/**
 * Starts Aster's graphical interface.
 *
 * <p>When JavaFX is loaded from the classpath rather than as modules, the class that
 * starts an application must not itself extend {@link Application}. This separate
 * launcher starts {@link Main} on its behalf, which is what lets the packaged JAR file
 * open the window.
 */
public final class Launcher {
    /**
     * Prevents instances being created, since this class only starts the interface.
     */
    private Launcher() {
    }

    /**
     * Opens the graphical interface.
     *
     * @param args command line arguments, passed on to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
