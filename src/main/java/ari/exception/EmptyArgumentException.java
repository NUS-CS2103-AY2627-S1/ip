package ari.exception;

/**
 * Signals that a task command is missing a required description or field.
 */
public class EmptyArgumentException extends Exception {
    /**
     * Creates an exception for the specified task type.
     *
     * @param taskType Type of task with missing information.
     */
    public EmptyArgumentException(String taskType) {
        super(String.format("Oh no! You can't have an empty description for %ss", taskType));
    }
}
