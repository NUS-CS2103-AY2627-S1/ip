package kenbot;

/**
 * Signals a problem caused by something the user typed.
 *
 * <p>Kenbot throws this whenever it cannot use a command, so the reason can be
 * reported in one place instead of at every spot that notices a problem.
 * Genuine programming mistakes are deliberately not represented by this class,
 * so they still crash and get noticed.</p>
 */
public class KenbotException extends Exception {

    /**
     * Creates an exception carrying an explanation for the user.
     *
     * @param message wording shown to the user, saying what went wrong
     */
    public KenbotException(String message) {
        super(message);
    }
}
