package walle.exceptions;

/**
 * Thrown when the user's input names a command/task type WALLE doesn't recognise.
 */
public class InvalidTaskTypeException extends WALLEException {

    /**
     * Creates an InvalidTaskTypeException for the given unrecognised type.
     *
     * @param taskType the unrecognised type name, e.g. the first word of the user's input.
     */
    public InvalidTaskTypeException(String taskType) {
        super("\n   ERROR :( Sorry...what is a " + taskType + "? That's not in my dictionary...\n");
    }

}
