package ralph.exception;

/**
 * Represents an error specific to Ralph's command parsing and validation.
 */
public class RalphException extends Exception {
    /**
     * Constructs a RalphException with the specified message.
     *
     * @param message the detail message
     */
    public RalphException(String message) {
        super(message);
    }
}
