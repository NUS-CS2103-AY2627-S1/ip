package chin.util;

/** Checked exception carrying a user-facing error message for the Chin chatbot. */
public class ChinException extends Exception {
    /** Creates the exception with a human-readable {@code message}. */
    public ChinException(String message) {
        super(message);
    }
}
