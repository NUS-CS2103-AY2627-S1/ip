package walle.exceptions;

/**
 * Thrown when a {@code deadline}/{@code event} command specifies the same
 * argument marker (e.g. {@code /by}, {@code /from}, {@code /to}) more than once.
 */
public class DuplicateArgException extends WALLEException {

    /**
     * Creates a DuplicateArgException for the given task type and repeated argument.
     *
     * @param taskType the task type the command was for, e.g. {@code "deadline"}.
     * @param argName  the name of the repeated argument marker, e.g. {@code "/by"}.
     */
    public DuplicateArgException(String taskType, String argName) {
        super("\n   ERROR :( Your " + taskType + " specifies " + argName
                + " more than once. That's not allowed!\n");
    }

}
