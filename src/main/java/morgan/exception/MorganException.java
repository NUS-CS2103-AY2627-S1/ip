package morgan.exception;

/**
 * Creates certain type of exception that caused by the invalid commands.
 */
public class MorganException extends Exception {
    public MorganException(String message) {
        super(message);
    }
}
