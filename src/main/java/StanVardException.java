/**
 * Represents an error caused by an invalid StanVard user command.
 */
public class StanVardException extends Exception {
    /**
     * Creates an exception with a user-facing error message.
     *
     * @param message explanation of how the user can correct the command
     */
    public StanVardException(String message) {
        super(message);
    }
}
