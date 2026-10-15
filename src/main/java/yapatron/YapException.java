package yapatron;

/**
 * Represents the exceptions thrown by Yapatron
 */
public class YapException extends Exception {

    /**
     * Creates a YapException instance with an error message
     *
     * @param message Custom exception message
     */
    public YapException(String message) {
        super(message);
    }

}
