package bubu.exception;

/** Indicates that a command received an argument it does not support. */
public class UnexpectedArgumentException extends BubuException {
    private static final String UNEXPECTED_ARGUMENT_MESSAGE =
            "Meow! The %s command does not accept arguments.";

    /**
     * Creates an error for an argument supplied to a command without arguments.
     *
     * @param commandName command that received the unexpected argument.
     */
    public UnexpectedArgumentException(String commandName) {
        super(String.format(UNEXPECTED_ARGUMENT_MESSAGE, commandName));
    }
}
