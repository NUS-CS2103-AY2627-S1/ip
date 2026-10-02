package florkofcows.exception;

/**
 * Represents an error specific to FlorkOfCows, such as invalid user input
 * or a failure while saving/loading tasks.
 */
public class FlorkingExceptions extends Exception {

    /**
     * Creates an exception with the given user-facing error message.
     *
     * @param message the error message to display.
     */
    public FlorkingExceptions(String message) {
        super(message);
    }
}
