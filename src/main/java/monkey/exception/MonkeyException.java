package monkey.exception;

/** Represents an error caused by invalid input to Monkey. */
public class MonkeyException extends Exception {
    /** Creates an input error with the given user-facing message. */
    public MonkeyException(String message) {
        super(message);
    }
}