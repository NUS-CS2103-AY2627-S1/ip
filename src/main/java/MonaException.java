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

    /**
     * Creates an exception whose message is followed by a "Hint:" line showing an example of
     * a correctly formed command, so the user can immediately see how to fix their input.
     *
     * @param message the message describing what went wrong.
     * @param hint an example command demonstrating the correct usage.
     * @return a {@code MonaException} combining the message and the hint.
     */
    public static MonaException withHint(String message, String hint) {
        return new MonaException(message + "\nHint: " + hint);
    }
}
