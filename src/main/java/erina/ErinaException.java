package erina;

/**
 * Signals that Erina understood the user well enough to know something was
 * wrong with the request, and can explain it.
 *
 * <p>This extends {@link Exception} rather than {@link RuntimeException},
 * which makes it a <em>checked</em> exception: the compiler forces every
 * caller either to handle it or to declare that it throws too. That is what
 * we want here, because the whole point of these errors is that the main
 * loop must report them to the user rather than let the program die.
 *
 * <p>The message carried by this exception is shown to the user as-is, so it
 * is written in plain language rather than as a technical description.
 */
public class ErinaException extends Exception {
    /**
     * Creates an exception carrying a message meant for the user.
     *
     * @param message what went wrong, phrased for the person typing
     */
    public ErinaException(String message) {
        super(message);
    }
}
