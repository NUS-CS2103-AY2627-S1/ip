package spoon.exception;

/**
 * Thrown when the input given by the user is missing argument(s).
 */
public class MissingArgumentException extends SpoonException {

    // Constructor
    public MissingArgumentException(String command, String argument) {
        super("Command " + command + " must have " + argument + ". Oops!");
    }
}
