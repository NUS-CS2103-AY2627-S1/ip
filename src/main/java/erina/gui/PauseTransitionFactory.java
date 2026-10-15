package erina.gui;

import javafx.animation.PauseTransition;
import javafx.util.Duration;

/**
 * Runs an action after a short pause on the JavaFX thread.
 *
 * <p>Used so that the farewell message stays on screen briefly before the
 * window closes, rather than the window vanishing the instant it appears.
 */
public class PauseTransitionFactory {
    /** How long the farewell stays on screen before the window closes. */
    private static final Duration EXIT_DELAY = Duration.seconds(1.2);

    /** This class is not meant to be instantiated; its method is static. */
    private PauseTransitionFactory() {
    }

    /**
     * Runs the given action after a short pause.
     *
     * @param action what to do once the pause is over
     */
    public static void runAfterPause(Runnable action) {
        PauseTransition pause = new PauseTransition(EXIT_DELAY);
        pause.setOnFinished(event -> action.run());
        pause.play();
    }
}
