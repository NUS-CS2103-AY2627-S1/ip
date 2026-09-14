package nova;

/**
 * One thing the chatbot says, together with whether it is reporting a problem.
 *
 * <p>The flag exists so a front end can present failures differently without
 * having to inspect the wording. The console front end ignores it; the GUI
 * colours error replies so a mistyped command is noticed rather than scrolling
 * past looking like a normal answer.
 *
 * @param text    what the chatbot says, possibly spanning several lines
 * @param isError whether this reply reports something going wrong
 */
public record Reply(String text, boolean isError) {

    /**
     * Wraps an ordinary reply.
     *
     * @param text what the chatbot says
     * @return a reply that is not an error
     */
    public static Reply of(String text) {
        return new Reply(text, false);
    }

    /**
     * Wraps a reply that reports a problem.
     *
     * @param text what went wrong
     * @return a reply marked as an error
     */
    public static Reply error(String text) {
        return new Reply(text, true);
    }

    /**
     * Returns whether the chatbot had nothing to say.
     *
     * @return true if there is no text to show
     */
    public boolean isEmpty() {
        return text.isEmpty();
    }
}
