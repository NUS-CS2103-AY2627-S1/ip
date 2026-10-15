package bubu.exception;

/**
 * Indicates that a command is missing a required argument.
 */
public class MissingArgumentException extends BubuException {
    private static final String MISSING_ARGUMENT_MESSAGE = "Meow! The argument for %s command is missing. Meow!";

    /**
     * Creates an error for a command with a missing argument.
     *
     * @param commandName command that requires the argument.
     */
    public MissingArgumentException(String commandName) {
        super(String.format(MISSING_ARGUMENT_MESSAGE, commandName));
    }
}
