package panda.exception.parser;

/**
 * Signals that a command argument has an invalid value.
 */
public class InvalidArgumentException extends ParseException {

    /**
     * Creates an exception with a custom error message.
     *
     * @param message Explanation of the argument problem.
     */
    public InvalidArgumentException(String message) {
        super(message);
    }

    /**
     * Creates an exception with a custom error message and underlying cause.
     *
     * @param message Explanation of the argument problem.
     * @param cause Error that caused this exception.
     */
    protected InvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates an exception that explains the expected command usage.
     *
     * @param command Command whose arguments are invalid.
     * @param usage Expected command format.
     */
    public InvalidArgumentException(String command, String usage) {
        super(String.format("That command was almost impressive. Almost. Use %s like this: \"%s\"",
                command, usage));
    }
}
