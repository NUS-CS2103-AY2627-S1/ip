package henry.exception;

/**
 * Represents a deadline containing an impossible calendar date.
 */
public class InvalidDateException extends Exception {
    /**
     * Creates an exception with guidance for correcting the date.
     */
    public InvalidDateException() {
        super("That date is incorrect. Please enter a valid calendar date.");
    }
}
