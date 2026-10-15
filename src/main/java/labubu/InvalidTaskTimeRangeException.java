package labubu;

/** Signals an event whose start is not earlier than its end. */
public class InvalidTaskTimeRangeException extends Exception {
    /** Creates an exception with the message shown for an invalid event range. */
    public InvalidTaskTimeRangeException() {
        super("Oops! An event must start before it ends.");
    }
}
