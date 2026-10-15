package jimbo.exception;

/**
 * Signals that the user entered an invalid command or argument, e.g. a
 * missing task description or a task number that does not exist.
 * Thrown from the command-parsing logic and caught centrally in
 * {@link Jimbo#main}, where the message is shown to the user.
 */
public class JimboException extends Exception {
    /**
     * Creates a JimboException with the given user-facing error message.
     */
    public JimboException(String message) {
        super(message);
    }
}
