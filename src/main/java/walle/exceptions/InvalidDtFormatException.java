package walle.exceptions;

/**
 * Thrown when a user-supplied datetime string can't be parsed as a valid
 * {@code yyyy-MM-dd HHmm} date/time -- either because it doesn't match that
 * shape, or because a value in it is out of range (e.g. an hour of 25, or a
 * day that doesn't exist in that month).
 */
public class InvalidDtFormatException extends WALLEException {

    /**
     * Creates an InvalidDtFormatException.
     */
    public InvalidDtFormatException() {
        super("\n   ERROR :( Please enter a valid date/time as yyyy-MM-dd HHmm, e.g. 2026-09-19 2359\n");
    }

}
