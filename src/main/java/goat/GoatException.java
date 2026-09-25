package goat;

/**
 * Represents an invalid command or command argument supplied by the user.
 */
public class GoatException extends Exception {
    /**
     * Creates an exception with a message that explains how to correct the input.
     *
     * @param message explanation of the input error
     */
    public GoatException(String message) {
        super(message);
    }
}
