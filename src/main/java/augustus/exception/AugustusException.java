package augustus.exception;

/**
 * Represents errors that occur while Augustus processes commands or tasks.
 */
public class AugustusException extends Exception {
    public AugustusException(String message) {
        super(message);
    }
}
