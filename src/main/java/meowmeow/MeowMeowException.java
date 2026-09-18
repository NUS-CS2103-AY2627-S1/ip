package meowmeow;

/**
 * Represents an error caused by invalid user input or a failed operation
 * that the chatbot can recover from and report to the user.
 */
public class MeowMeowException extends Exception {

    /**
     * Constructs an exception carrying a message intended for the user.
     *
     * @param message Explanation shown to the user.
     */
    public MeowMeowException(String message) {
        super(message);
    }
}
