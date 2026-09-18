package martin.exception;

/**
 * Represents an exception thrown when an unrecognized or invalid command is
 * encountered.
 */
public class IllegalCommandException extends MartinException {

    /**
     * Constructs an {@code IllegalCommandException} with the specified detail
     * message.
     *
     * @param message The detail error message.
     */
    public IllegalCommandException(String message) {
        super(message);
    }

}
