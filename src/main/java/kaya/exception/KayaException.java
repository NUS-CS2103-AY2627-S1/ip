package kaya.exception;

/**
 * Represents an error caused by an invalid command given to Kaya.
 */
public class KayaException extends Exception {
    /**
     * Creates an exception with a message suitable for showing to the user.
     *
     * @param message an explanation of the invalid command
     */
    public KayaException(String message) {
        super(message);
    }
}
