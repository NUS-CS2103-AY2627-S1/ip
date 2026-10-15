package spoon.exception;

/**
 * Thrown when the command given by the user is invalid.
 */
public class InvalidCommandException extends SpoonException {

    // Constructor
    public InvalidCommandException() {
        super("Uh oh, I don't know what that means. Please enter a valid command!");
    }
}
