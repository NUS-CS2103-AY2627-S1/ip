package moon;

/**
 * Represents an error caused by an invalid command entered into Moon.
 */
public class MoonException extends Exception {
    /**
     * Creates an exception with a message that explains how to fix the command.
     *
     * @param message the error message shown to the user
     */
    public MoonException(String message) {
        super(message);
    }
}
