package cxception;

/**
 * Signals that a date/time string could not be parsed into a valid format.
 */
public class InvalidDateException extends Exception {
    /**
     * Constructs an {@code InvalidDateException} with a default message
     * describing the accepted date/time formats.
     */
    public InvalidDateException() {
        super("Invalid date format! Use yyyy-MM-dd HHmm (e.g. 2026-08-27 1830) or yyyy-MM-dd for date-only.");
    }
}
