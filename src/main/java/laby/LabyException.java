package laby;

/** Represents an expected application or user-input error. */
public class LabyException extends Exception {
    /**
     * Creates an exception with a user-facing error message.
     *
     * @param message Error message to expose to the user.
     */
    public LabyException(String message) {
        super(message);
    }
}
