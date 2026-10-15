package ayre.exceptions;

/**
 * Thrown when String read from save file does not adhere to expected correctness standards.
 */
public class TaskLogCorruptedException extends AyreException {
    /**
     * Initializes the exception by passing the message on.
     *
     * @param e Error message.
     */
    public TaskLogCorruptedException(String e) {
        super(e);
    }
}
