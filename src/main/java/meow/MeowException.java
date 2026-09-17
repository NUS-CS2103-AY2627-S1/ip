package meow;

/**
 * Represents an exception caused by invalid user input or chatbot operation.
 */
public class MeowException extends Exception {

    /**
     * Creates a MeowException with the specified error message.
     *
     * @param message the error message describing the problem
     */
    public MeowException(String message) {
        super(message);
    }
}
