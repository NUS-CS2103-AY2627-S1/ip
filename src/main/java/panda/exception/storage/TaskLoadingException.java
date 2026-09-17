package panda.exception.storage;

import panda.exception.ApplicationException;

/**
 * Signals that Panda could not load saved tasks and must start with an empty task list.
 */
public class TaskLoadingException extends ApplicationException {

    /**
     * Creates an exception describing why saved tasks could not be loaded.
     *
     * @param message User-facing explanation of the loading failure.
     * @param cause Error that caused the loading failure.
     */
    public TaskLoadingException(String message, Throwable cause) {
        super(message, cause);
    }
}
