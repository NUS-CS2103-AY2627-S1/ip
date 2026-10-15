package charliek.exception;

/**
 * Indicates that a deadline or event command is missing a required parameter.
 */
public class EmptyParameterException extends CharlieKException {
    /**
     * The message displayed when a required task parameter is missing.
     */
    private static final String MESSAGE =
            "The parameter is empty! Enter the required parameters and let's get this done!";

    /**
     * Creates an exception for a missing deadline or event parameter.
     */
    public EmptyParameterException() {
        super(MESSAGE);
    }
}
