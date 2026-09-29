package emma;

/**
 * Signals that a command could not be carried out, carrying the message
 * Emma should show the user.
 */
public class EmmaException extends Exception {

    /**
     * Creates an exception whose message is shown to the user as-is.
     *
     * @param message what Emma should say about the problem.
     */
    public EmmaException(String message) {
        super(message);
    }
}
