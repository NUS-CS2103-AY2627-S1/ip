package jasper;

/**
 * Represents a custom exception specific to the Jasper application.
 */
public class JasperException extends Exception {
    /**
     * Constructs a JasperException with the specified error message.
     *
     * @param message Detailed error message.
     */
    public JasperException(String message) {
        super(message);
    }
}
