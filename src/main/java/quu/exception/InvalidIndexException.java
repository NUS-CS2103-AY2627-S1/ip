package quu.exception;

/**
 * Thrown when a command that needs a task number is given something that is not a number.
 */
public class InvalidIndexException extends QuuException {

    /**
     * Creates the exception, quoting the offending text back to the user.
     *
     * @param input the text that was not a task number
     */
    public InvalidIndexException(String input) {
        super("\"" + input + "\" isn't a task number");
    }
}
