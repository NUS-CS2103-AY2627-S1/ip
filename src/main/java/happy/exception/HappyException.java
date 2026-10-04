package happy.exception;

/**
 * Custom exception class specific to the Happy chatbot application.
 * Used for domain-specific error handling such as invalid input or missing parameters.
 */
public class HappyException extends Exception {
    /**
     * Constructs a HappyException with the specified detailed error message.
     *
     * @param message Detailed error explanation for the user.
     */
    public HappyException(String message) {
        super(message);
    }
}
