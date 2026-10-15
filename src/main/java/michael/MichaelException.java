package michael;

/**
 * Represents an error caused by an invalid michael.Michael command.
 */
public class MichaelException extends Exception {
    /**
     * Creates an exception with a message that can be shown to the user.
     *
     * @param message an explanation of the invalid command
     */
    public MichaelException(String message) {
        super(message);
    }
}
