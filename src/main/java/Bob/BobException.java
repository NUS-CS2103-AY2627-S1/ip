package bob;

/** Represents an expected error caused by an invalid user command. */
public class BobException extends Exception {
    /**
     * Creates an exception with a user-facing message.
     *
     * @param message User-facing error message.
     */
    public BobException(String message) {
        super(message);
    }
}
