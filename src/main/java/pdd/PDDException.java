package pdd;

/** Signals a user-facing error, e.g. invalid command input, that should be reported without crashing the program. */
public class PDDException extends Exception {
    /** Creates an exception carrying the given user-facing error message. */
    public PDDException(String message) {
        super(message);
    }
}
