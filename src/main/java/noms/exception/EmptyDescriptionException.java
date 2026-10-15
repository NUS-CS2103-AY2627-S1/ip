package noms.exception;

/**
 * Indicates that a task was created without a description.
 */
public class EmptyDescriptionException extends NomsException {
    /**
     * Creates the exception for a task created without a description.
     *
     * @param taskType the kind of task attempted (e.g. {@code "todo"})
     * @param usage the correct command format to show the user
     */
    public EmptyDescriptionException(String taskType, String usage) {
        super("This " + taskType
                + " is missing its main ingredient: a description.\n"
                + "Try: " + usage);
    }
}
