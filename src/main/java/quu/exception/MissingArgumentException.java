package quu.exception;

/**
 * Thrown when a command is missing an argument it needs.
 */
public class MissingArgumentException extends QuuException {

    /**
     * Creates the exception, showing the user the format the command expects.
     *
     * @param usage the expected form of the command, for example {@code todo <task>}
     */
    public MissingArgumentException(String usage) {
        super("Invalid format. Please follow this format: " + usage);
    }
}
