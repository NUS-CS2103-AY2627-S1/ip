package walle.exceptions;

/**
 * Thrown when a {@code todo}/{@code deadline}/{@code event} command is missing its description.
 */
public class MissingDescException extends WALLEException {

    /**
     * Creates a MissingDescException for the given task type.
     *
     * @param taskType the task type whose description is missing, e.g. {@code "todo"}.
     */
    public MissingDescException(String taskType) {
        super("\n   ERROR :( Your " + taskType + " has an empty description. That's not allowed!\n");
    }

}
