package bany.exceptions;

/** Signals that persisted task data does not follow Bany's expected format. */
public class TaskDataFormatException extends BanyException {
    /**
     * Creates an exception describing invalid persisted task data.
     *
     * @param message explanation of the invalid data.
     */
    public TaskDataFormatException(String message) {
        super(message);
    }
}
