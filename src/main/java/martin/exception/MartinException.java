package martin.exception;

/**
 * Represents an application-level failure that Martin can report to the user.
 */
public class MartinException extends RuntimeException {

    /**
     * Constructs a {@code MartinException} with the specified detail message.
     *
     * @param message The detail error message.
     */
    public MartinException(String message) {
        super(message);
    }

    /**
     * Constructs a {@code MartinException} with a detail message and underlying
     * cause.
     *
     * @param message The detail error message.
     * @param cause   The underlying cause of this exception.
     */
    public MartinException(String message, Throwable cause) {
        super(message, cause);
    }
}
