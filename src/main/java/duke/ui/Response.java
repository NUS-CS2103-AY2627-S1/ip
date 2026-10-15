package duke.ui;

/**
 * Carries a reply and its status so the GUI need not guess from message wording.
 *
 * @param text Formatted reply, including CLI separators.
 * @param isError Whether the reply reports a command or storage error.
 * @param isExit Whether a successful command ends the conversation.
 */
public record Response(String text, boolean isError, boolean isExit) {
    /**
     * Removes only CLI separator lines, keeping task descriptions and line breaks intact.
     *
     * @return Compact text suitable for a GUI message card.
     */
    public String displayText() {
        String separator = "____________________________________________________________";
        String prefix = separator + "\n";
        String suffix = "\n" + separator;
        if (text.startsWith(prefix) && text.endsWith(suffix)) {
            return text.substring(prefix.length(), text.length() - suffix.length());
        }
        return text;
    }
}
