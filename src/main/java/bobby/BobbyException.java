package bobby;

/** Represents an error caused by invalid user input in Bobby. */
public class BobbyException extends Exception {

    /**
     * Creates an exception with the message to show to the user.
     *
     * @param message user-facing explanation of the error
     */
    public BobbyException(String message) {
        super(message);
    }
}
