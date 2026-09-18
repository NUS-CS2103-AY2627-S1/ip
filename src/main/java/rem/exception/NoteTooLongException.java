package rem.exception;

/**
 * Indicates that a note exceeds Rem's maximum supported length.
 */
public class NoteTooLongException extends RemException {
    private static final String MESSAGE = "Rem can't remember more than 200 characters...";

    /**
     * Creates an exception for a note longer than 200 Unicode code points.
     */
    public NoteTooLongException() {
        super(MESSAGE);
    }
}
