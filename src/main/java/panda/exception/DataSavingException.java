package panda.exception;

import java.io.IOException;

/**
 * Signals that Panda could not save the current task list.
 *
 * Represents an expected storage failure without hiding
 * unrelated programming errors.
 */
public class DataSavingException extends PandaException {
    /**
     * Creates the standard panda-themed saving error.
     *
     * @param cause the underlying file-access problem.
     */
    public DataSavingException(IOException cause) {
        super("OOPS!!! This panda could not save its bamboo archive.");
        initCause(cause);
    }
}
