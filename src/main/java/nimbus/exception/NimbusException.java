package nimbus.exception;

/**
 * Represents an exception specific to the Nimbus application.
 */
public class NimbusException extends Exception {

    /**
     * Constructs a new NimbusException with the specified detail message.
     *
     * @param message the detail message.
     */
    public NimbusException(String message) {
        super(message);
    }
}
