// javadocs created by AI

package baron.exception;

/**
 * Signals that an error occurred while accessing a file.
 */
public class FileException extends BaronException {
    /**
     * Creates a file exception for a specific file path.
     *
     * @param filePath the path of the file that could not be accessed.
     */
    public FileException(String filePath) {
        super("The Barathos ledger could not be opened: " + filePath
                + ". Your records remain untouched; check the file and try again.");
    }
}
