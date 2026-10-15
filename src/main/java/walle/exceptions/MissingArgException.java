package walle.exceptions;

/**
 * Thrown when a {@code deadline}/{@code event} command is missing a required
 * argument, e.g. the {@code /by}, {@code /from}, or {@code /to} marker.
 */
public class MissingArgException extends WALLEException {

    /**
     * Creates a MissingArgException for the given task type and missing argument.
     *
     * @param taskType the task type the command was for, e.g. {@code "deadline"}.
     * @param argName  the name of the missing argument marker, e.g. {@code "/by"}.
     */
    public MissingArgException(String taskType, String argName) {
        super("\n   ERROR :( Your " + taskType + " is missing the " + argName
                + " argument. That's not allowed!\n");
    }

}
