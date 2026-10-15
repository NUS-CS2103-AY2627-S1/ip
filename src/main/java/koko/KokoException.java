package koko;

/**
 * Represents a problem caused by an invalid koko.Koko command or its arguments.
 */
public class KokoException extends Exception {
    /**
     * Creates an exception with a message that explains how the user can correct the command.
     *
     * @param message the user-friendly error message
     */
    public KokoException(String message) {
        super(message);
    }
}
