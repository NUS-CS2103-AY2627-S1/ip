package wobble.exceptions;

/** Represents an error caused by an invalid Wobble command. */
public class WobbleException extends Exception {
    private static final long serialVersionUID = 1L;
    private final boolean includeCommandSuggestion;

    /** Creates an exception with a message explaining how to fix the command. */
    public WobbleException(String message) {
        this(message, true);
    }

    /** Creates an exception and controls whether the UI should append a command suggestion. */
    public WobbleException(String message, boolean includeCommandSuggestion) {
        super(message);
        this.includeCommandSuggestion = includeCommandSuggestion;
    }

    /** Returns whether this error should include a suggested command in the user interface. */
    public boolean shouldSuggestCommand() {
        return includeCommandSuggestion;
    }
}
