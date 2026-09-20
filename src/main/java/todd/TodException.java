package todd;

/** Represents an error that Todd can explain to the user. */
public class TodException extends Exception {
    /** Creates an exception containing the specified user-facing message. */
    public TodException(String message) {
        super(message);
    }
}
