package jassabot.exception;

/**
 * Represents an error caused by invalid input to the JassaBot chatbot.
 */
public class JassaBotException extends Exception {
    /**
     * Creates an exception with a message that explains how the user can correct their input.
     *
     * @param message Error message shown to the user.
     */
    public JassaBotException(String message) {
        super(message);
    }
}
