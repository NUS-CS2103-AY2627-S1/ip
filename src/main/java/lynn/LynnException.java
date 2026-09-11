package lynn;

/** Represents an expected error caused by invalid user input or task data. */
public class LynnException extends Exception {
    /**
     * Creates an exception with a message that can be shown to the user.
     *
     * @param message the explanation of the error
     */
    public LynnException(String message) {
        super(message);
    }
}
