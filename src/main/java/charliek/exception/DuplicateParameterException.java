package charliek.exception;

/**
 * Indicates that a command includes a parameter more than once.
 */
public class DuplicateParameterException extends CharlieKException {
    /**
     * Creates an exception for a repeated command parameter.
     *
     * @param parameter the repeated parameter marker.
     * @param usage the valid command usage.
     */
    public DuplicateParameterException(String parameter, String usage) {
        super("The parameter " + parameter + " was specified more than once. Use: " + usage);
    }
}
