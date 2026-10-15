package suki;

/**
 * Represents an error caused by invalid user input that Suki cannot act on.
 */
public class SukiException extends Exception {
    /**
     * Creates an exception carrying a message written for the user rather than
     * for a developer, since it is shown to them directly.
     *
     * @param message an explanation the user can act on
     */
    public SukiException(String message) {
        super(message);
    }
}
