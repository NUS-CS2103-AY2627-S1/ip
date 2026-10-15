package ace.exception;

/**
 * Base class for every checked error that can arise from interpreting or acting on
 * a user command. Caught in a single place in the main loop so any subtype is
 * reported to the user the same way, without the loop needing to know about each
 * specific kind of error.
 */
public class AceException extends Exception {

    /**
     * Creates a new exception with the given user-facing error message.
     *
     * @param msg the message to show the user describing what went wrong.
     */
    public AceException(String msg) {
        super(msg);
    }
}
