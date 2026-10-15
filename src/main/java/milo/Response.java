package milo;

/** Carries a reply's text, error status, and optional recovery guidance to the interface. */
public record Response(String message, boolean isError, String suggestion) {
    /** Returns the reply with recovery guidance when it is available. */
    public String displayText() {
        return suggestion.isEmpty() ? message : message + "\n\n" + suggestion;
    }
}
