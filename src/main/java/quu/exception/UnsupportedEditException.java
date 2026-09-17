package quu.exception;

/**
 * Thrown when an {@code edit} command names a detail the task does not have, such as
 * {@code /by} on a to-do.
 *
 * <p>This is not a missing argument: the user supplied an argument, it just does not
 * belong to this kind of task. Naming the offending flag matters, because the usage line
 * on its own never says which of several edits was the problem.
 */
public class UnsupportedEditException extends QuuException {

    /**
     * Creates the exception, naming the flag the task does not have.
     *
     * @param flag the edit flag the user gave, without its leading slash
     * @param usage the expected form of the command, shown to the user
     */
    public UnsupportedEditException(String flag, String usage) {
        super(String.format("This task has no /%s. Use: %s", flag, usage));
    }
}
