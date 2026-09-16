package lebronjames;

/**
 * Represents an input error specific to the Lebron James chatbot.
 */
public class LebronJamesException extends Exception {
    /**
     * Creates an exception with an explanation that can be shown to the user.
     *
     * @param message Explanation of the input error.
     */
    public LebronJamesException(String message) {
        super(message);
    }
}
