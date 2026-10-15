package littler.exception;

/**
 * Represents custom exceptions specific to the LittleR application domain.
 * Extends {@link Exception} to handle application-specific runtime errors and validation failures.
 */
public class LittleRException extends Exception {

    /**
     * Constructs a new LittleRException with the specified detail error message.
     *
     * @param message the detail message explaining the cause of the exception
     */
    public LittleRException(String message) {
        super(message);
    }
}
