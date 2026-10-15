package charliek.exception;

/**
 * Indicates that a command does not follow the syntax expected by CharlieK.
 */
public class InvalidCommandFormatException extends CharlieKException {
    /**
     * Creates an exception with the general command-format guidance.
     */
    public InvalidCommandFormatException() {
        super("That command format is not winning. Use 'help' to check the playbook.");
    }

    /**
     * Creates an exception with the usage expected for a command.
     *
     * @param usage the valid command usage.
     */
    public InvalidCommandFormatException(String usage) {
        super("That command format is not winning. Use: " + usage);
    }
}
