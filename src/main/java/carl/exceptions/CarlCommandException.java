package carl.exceptions;

/**
 * Represents an exception thrown when a user enters an invalid command format or provides incorrect arguments.
 */
public class CarlCommandException extends CarlException {

    /**
     * Constructs a new {@code CarlCommandException} with a formatted error message
     * showing the correct usage.
     *
     * @param usage The correct usage format or the reason for the command failure.
     */
    public CarlCommandException(String usage) {
        super(String.format("Command error: %s", usage));
    }
}
