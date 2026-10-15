package food.exception;

/**
 * Signals that the user typed something the chatbot cannot make sense of, such as an unknown
 * command, a missing argument, or a task number that does not exist.
 *
 * <p>This is an expected, recoverable situation: the caller should show the message and carry
 * on reading commands. Nothing is broken.
 */
public class FoodInputException extends FoodException {
    /**
     * Creates an exception describing what about the user's input could not be understood.
     *
     * @param msg the explanation to show the user, e.g. which command was not understood.
     */
    public FoodInputException(String msg) {
        super(msg);
    }

    /**
     * Creates an exception that also remembers the lower-level failure behind it.
     *
     * @param msg   the explanation to show the user.
     * @param cause the lower-level exception being translated, e.g. the NumberFormatException
     *              from a task number that was not a number.
     */
    public FoodInputException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
