/**
 * Represents an error that Mona recognizes while reading user input, such as a missing
 * description or an invalid task number. The message carried by this exception is shown
 * directly to the user, so it should explain what went wrong and how to fix it.
 */
public class MonaException extends Exception {
    /**
     * Creates an exception with the given user-facing message.
     *
     * @param message the message describing the error, shown directly to the user.
     */
    public MonaException(String message) {
        super(message);
    }
}
