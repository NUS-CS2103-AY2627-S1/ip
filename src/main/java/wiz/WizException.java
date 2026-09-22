package wiz;

/**
 * Custom checked exception representing application-specific errors in Wiz.
 */
public class WizException extends Exception {

    /**
     * Constructs a WizException with the specified error message.
     *
     * @param message The detailed error message.
     */
    public WizException(String message) {
        super(message);
    }
}