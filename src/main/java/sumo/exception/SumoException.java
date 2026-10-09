package sumo.exception;

/**
 * Represents an error caused by an invalid command entered in Sumo.
 */
public class SumoException extends Exception {
    /**
     * Creates an exception with a message that can be shown to the user.
     *
     * @param message an explanation of how to correct the command.
     */
    public SumoException(String message) {
        super(message);
    }
}
