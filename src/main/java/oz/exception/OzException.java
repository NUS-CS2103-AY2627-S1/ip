package oz.exception;

/**
 * Custom checked exception representing user input errors or runtime failures in Oz.
 */
public class OzException extends Exception {

    /**
     * Constructs an OzException with the specified error detail message.
     *
     * @param message Explanatory message describing the error.
     */
    public OzException(String message) {
        super(message);
    }
}
