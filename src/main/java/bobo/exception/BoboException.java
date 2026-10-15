package bobo.exception;

/**
 * Represents exceptions specific to the Bobo chatbot application.
 */
public class BoboException extends Exception {

    /**
     * Constructs a BoboException with the specified error message.
     *
     * @param message The detailed error message.
     */
    public BoboException(String message) {
        super(message);
    }
}
