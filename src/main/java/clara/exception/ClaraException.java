package clara.exception;

/**
 * Represents an exception specific to Clara application operations.
 */
public class ClaraException extends Exception {

    /**
     * Constructs a ClaraException with the specified error message.
     *
     * @param errorMessage the detail message explaining the reason for the exception
     */
    public ClaraException(String errorMessage) {
        super(errorMessage);
    }
}
