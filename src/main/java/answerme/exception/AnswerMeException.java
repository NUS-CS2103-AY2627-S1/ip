package answerme.exception;

/**
 * Represents an exception caused by invalid user input or an application
 * operation that cannot be completed.
 */
public class AnswerMeException extends Exception {
    /**
     * Constructs a new custom exception object with the specified
     * error message.
     *
     * @param message The message describing the exception.
     */
    public AnswerMeException(String message) {
        super(message);
    }
}
