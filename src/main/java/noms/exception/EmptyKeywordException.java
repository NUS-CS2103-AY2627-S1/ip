package noms.exception;

/**
 * Indicates that a {@code find} command was given without a keyword to
 * search for.
 */
public class EmptyKeywordException extends NomsException {
    /**
     * Creates the exception for a {@code find} command given without a keyword.
     *
     * @param usage the correct command format to show the user.
     */
    public EmptyKeywordException(String usage) {
        super("Noms can't sniff out a task without a scent!\n"
                + "Tell Noms a keyword to hunt for.\n"
                + "Try: " + usage);
    }
}
