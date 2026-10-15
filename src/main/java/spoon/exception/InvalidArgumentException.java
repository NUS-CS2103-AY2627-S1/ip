package spoon.exception;

/**
 * Thrown when the input given by the user is an invalid argument.
 */
public class InvalidArgumentException extends SpoonException {

    // Constructor
    public InvalidArgumentException(String message) {
        super("That seems to be an error. " + message);
    }
}
