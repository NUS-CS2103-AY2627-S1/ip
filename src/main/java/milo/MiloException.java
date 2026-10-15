package milo;

/** Represents an expected error while processing a Milo command or task. */
public class MiloException extends Exception {
    private static final long serialVersionUID = 1L;
    private final String suggestion;

    /** Creates an exception with a user-facing explanation. */
    public MiloException(String message) {
        this(message, "");
    }

    /** Creates an exception with guidance for correcting the failed command. */
    public MiloException(String message, String suggestion) {
        super(message);
        this.suggestion = suggestion;
    }

    public String getSuggestion() {
        return suggestion;
    }
}
