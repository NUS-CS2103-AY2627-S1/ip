package panda.exception;

/**
 * Represents an expected input or command error reported by Panda.
 */
public class PandaException extends Exception {
    private final ErrorType errorType;

    /**
     * Creates an exception for a typed Panda error.
     *
     * @param errorType the category of the error
     * @param arguments values used by the error message template
     */
    public PandaException(ErrorType errorType, Object... arguments) {
        super(errorType.getMessage(arguments));
        this.errorType = errorType;
    }

    /**
     * Returns the structured error category.
     *
     * @return the error type
     */
    public ErrorType getErrorType() {
        return errorType;
    }
}
