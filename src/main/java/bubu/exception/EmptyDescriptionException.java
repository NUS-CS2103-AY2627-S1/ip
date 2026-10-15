package bubu.exception;

/**
 * Indicates that a task command does not contain a description.
 */
public class EmptyDescriptionException extends BubuException {
    private static final String EMPTY_DESCRIPTION_ERROR_MESSAGE = "Meow! The description of a %s task cannot be empty.";

    /**
     * Creates an error message for a command with no description.
     *
     * @param commandName command that requires a description.
     */
    public EmptyDescriptionException(String commandName) {
        super(String.format(EMPTY_DESCRIPTION_ERROR_MESSAGE, commandName));
    }
}
