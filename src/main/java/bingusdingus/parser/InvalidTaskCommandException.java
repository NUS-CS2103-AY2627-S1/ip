package bingusdingus.parser;

/**
 * Thrown when a user enters a task command with an invalid format.
 */
public class InvalidTaskCommandException extends Exception {
    /** Creates an exception with a description of the invalid command. */
    public InvalidTaskCommandException(String message) {
        super(message);
    }
}
