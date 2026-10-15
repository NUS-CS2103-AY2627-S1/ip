package bob;

import bob.ui.Main;
import javafx.application.Application;

/**
 * Starts the chatbot in a window.
 *
 * <p>This is the way into the graphical chatbot, as {@link Bob#main} is the way
 * into the console one. It does nothing but start the other, which looks like a
 * class that need not exist — and it would not, if the program were always run as
 * a set of modules.
 *
 * <p>It exists because of how JavaFX starts. When the JavaFX classes are found on
 * the class path rather than the module path, a program whose main class extends
 * {@link Application} refuses to start, complaining that the JavaFX runtime
 * components are missing. Launching from a class that does <em>not</em> extend
 * {@code Application} avoids the check, which is why the one line below is written
 * here instead of in {@link Main}.
 *
 * <p>That situation is the one a packaged jar is usually run in, so keeping this
 * class means the jar starts wherever it is taken rather than only where the
 * JavaFX modules happen to be set up.
 */
public class Launcher {

    /**
     * Starts the windowed chatbot.
     *
     * @param args command line arguments, which are passed on to JavaFX and which
     *             this chatbot does not use itself.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
