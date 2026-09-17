package panda.exception.storage;

import panda.exception.ApplicationException;

/**
 * Signals that Panda could not save the current task list.
 */
public class TaskSavingException extends ApplicationException {

    /**
     * Creates an exception describing why the current task list could not be saved.
     *
     * @param message User-facing explanation of the saving failure.
     * @param cause Error that caused the saving failure.
     */
    public TaskSavingException(String message, Throwable cause) {
        super(message, cause);
    }
}
