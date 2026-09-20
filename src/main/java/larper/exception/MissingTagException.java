package larper.exception;

/**
 * Reports that a tag command did not provide a tag name.
 */
public class MissingTagException extends LarperException {
    private static final String MESSAGE = " Larper needs a tag name there. Try a one-word era like #school.";

    /**
     * Creates an exception for a tag command without a tag name.
     */
    public MissingTagException() {
        super(MESSAGE);
    }
}
