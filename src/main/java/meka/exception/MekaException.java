package meka.exception;

/**
 * Represents an invalid command entered by the user.
 */
public class MekaException extends Exception {

    /**
     * Creates an exception with a message that can be shown to the user.
     *
     * @param message explanation of why the command is invalid.
     */
    public MekaException(String message) {
        super(message);
    }
}
