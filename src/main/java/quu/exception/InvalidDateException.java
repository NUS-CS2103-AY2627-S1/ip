package quu.exception;

/**
 * Thrown when text meant to be a date cannot be read as one.
 */
public class InvalidDateException extends QuuException {

    /**
     * Creates the exception, quoting the offending text back to the user.
     *
     * @param input the text that could not be read as a date
     */
    public InvalidDateException(String input) {
        super("'" + input + "' is not a valid date. Use yyyy-mm-dd, e.g. 2026-06-06.");
    }
}
