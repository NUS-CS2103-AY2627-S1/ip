package bubu.exception;

/** Indicates that a task description contains unsupported characters. */
public class InvalidDescriptionException extends BubuException {
    private static final String INVALID_DESCRIPTION_MESSAGE =
            "Meow! Task descriptions cannot contain '|' or line breaks.";

    /** Creates an error for a description that cannot be safely stored. */
    public InvalidDescriptionException() {
        super(INVALID_DESCRIPTION_MESSAGE);
    }
}
