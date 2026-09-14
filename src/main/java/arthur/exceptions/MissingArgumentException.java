package arthur.exceptions;

/**
 * Indicates that a command is missing a required argument.
 */
public class MissingArgumentException extends ArthurRuntimeException {
    public MissingArgumentException(String commandName, String missingArgument) {
        super("Missing argument from " + commandName + ": " + missingArgument);
    }
}
