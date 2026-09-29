package emma.gui;

import javafx.application.Application;

/**
 * Starts the window.
 *
 * <p>The program is launched from here rather than from {@link Main} because a class that
 * extends {@code Application} cannot be the main class of a jar unless the JavaFX modules
 * are on the module path. Going through a plain class sidesteps that.
 */
public class Launcher {

    /**
     * Starts Emma's window.
     *
     * @param args passed on to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
