package jeremy.exception;

/**
 * Thrown when the user's input can't be understood or acted on —
 * e.g. an unknown command, a missing task description, or a
 * task number that doesn't exist. The message is shown to the
 * user as-is, so it should be a friendly, complete sentence.
 */
public class JeremyException extends Exception {
    public JeremyException(String message) {
        super(message);
    }
}
