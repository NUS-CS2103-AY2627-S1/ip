package panda.exception;

/**
 * Signals that Panda could not safely load its stored task data.
 *
 * Converts storage format and file-access problems into a
 * focused error that can be reported at the command-line UI boundary.
 */
public class DataLoadingException extends PandaException {
    /**
     * Creates an error for a malformed line in the data file.
     *
     * @param lineNumber the one-based line number containing invalid data.
     * @param reason a short explanation of the invalid data.
     */
    public DataLoadingException(int lineNumber, String reason) {
        super("Line " + lineNumber + " has " + reason);
    }

    /**
     * Creates an error for a data file that cannot be opened or read.
     *
     * @param filePath the path Panda attempted to read.
     * @param cause the underlying file-access problem.
     */
    public DataLoadingException(String filePath, Throwable cause) {
        super("OOPS!!! This panda cannot read its bamboo archive at " + filePath + ".");
        initCause(cause);
    }
}
