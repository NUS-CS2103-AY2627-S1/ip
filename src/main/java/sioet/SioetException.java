package sioet;

/**
 * Represents an error caused by an invalid command entered into sioet.ui.Sioet.
 */
public class SioetException extends Exception {
    /**
     * Creates an exception with a message that explains how the user can fix the input.
     *
     * @param message the user-friendly error message
     */
    public SioetException(String message) {
        super(message);
    }
}
