package sunnyexception;

/**
 * Indicates that the user provided an invalid date.
 * This class extends {@link SunnyException}.
 */
public class IncorrectDateFormatException extends SunnyException {
    /**
     * Constructs the exception with the specified message.
     *
     * @param message the message describing the exception.
     */
    public IncorrectDateFormatException(String message) {
        super(message);
    }
}
