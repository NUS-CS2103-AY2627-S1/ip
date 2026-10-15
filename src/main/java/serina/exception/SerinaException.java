package serina.exception;

/**
 * Represents errors that Serina can explain to the user.
 */
public class SerinaException extends Exception {
    /** Serina-specific error represented by this exception. */
    private final SerinaError error;

    /**
     * Creates a Serina-specific exception with the given error.
     *
     * @param error Error that explains what went wrong.
     */
    public SerinaException(SerinaError error) {
        super(error.getMessage());
        this.error = error;
    }

    /**
     * Creates a Serina-specific exception with additional user-facing context.
     *
     * @param error Error category.
     * @param details Details appended to the standard message.
     */
    public SerinaException(SerinaError error, String details) {
        super(error.getMessage() + " " + details);
        this.error = error;
    }

    /**
     * Creates a Serina-specific exception that retains its technical cause.
     *
     * @param error Error category.
     * @param cause Underlying failure.
     */
    public SerinaException(SerinaError error, Throwable cause) {
        super(error.getMessage(), cause);
        this.error = error;
    }

    /**
     * Returns whether Serina should exit after this exception is shown.
     *
     * @return {@code true} if this exception should end the application.
     */
    public boolean shouldExit() {
        return error.shouldExit();
    }
}
