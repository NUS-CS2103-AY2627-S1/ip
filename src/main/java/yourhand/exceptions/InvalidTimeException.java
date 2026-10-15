package yourhand.exceptions;

/** Represents a time outside the supported 24-hour range or format. */
public class InvalidTimeException extends YourHandException {
    /** Creates an invalid-time exception for the supplied invalid time text. */
    public InvalidTimeException(String invalidTime) {
        super("Time [" + invalidTime + "] is out of bounds. "
                + "How extra time just spawn out of nowhere in your day, please keep it within 24 hours"
                + " for your time");
    }
}
