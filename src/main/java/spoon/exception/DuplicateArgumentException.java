package spoon.exception;

/**
 * Thrown when the input given by the user has duplicate arguments.
 */
public class DuplicateArgumentException extends SpoonException {

    // Constructor
    public DuplicateArgumentException(String delimiter) {
        super("Seems like you have multiple /" + delimiter + " arguments...");
    }
}
