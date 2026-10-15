package noms.task;

/**
 * Represents a task in Noms' task list.
 *
 * A task owns both its description and completion status so that task-related
 * state changes are handled by the task itself.
 */
public class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates a new incomplete task with the given description.
     *
     * @param description the text describing the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the text used to display this task's completion status.
     *
     * @return {@code X} for a completed task, or a space otherwise
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Marks this task as completed.
     *
     * @return {@code true} if the task changed from incomplete to complete
     */
    public boolean markAsDone() {
        boolean changed = !isDone;
        isDone = true;
        return changed;
    }

    /**
     * Marks this task as incomplete.
     *
     * @return {@code true} if the task changed from complete to incomplete
     */
    public boolean markAsNotDone() {
        boolean changed = isDone;
        isDone = false;
        return changed;
    }

    /**
     * Returns this task's description.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether another task has the same user-defined details.
     * Completion status is deliberately excluded because marking a task does
     * not turn it into a distinct task.
     *
     * @param other the task to compare with
     * @return {@code true} if both tasks have the same type and description
     */
    public boolean hasSameDetailsAs(Task other) {
        return other != null
                && getClass().equals(other.getClass())
                && description.equals(other.description);
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }

    /**
     * Returns this task's representation for saving to disk, as a
     * pipe-separated line: {@code <done flag> | <description>}.
     *
     * Subclasses prepend their type letter and append any extra fields
     * (e.g. a deadline's date or an event's start/end times), mirroring
     * how {@link #toString()} is built up via {@code super.toString()}.
     *
     * @return the save-file line for this task, without its type marker
     */
    public String toFileFormat() {
        return (isDone ? "1" : "0") + " | " + escape(description);
    }

    /**
     * Escapes a field's text before it is embedded in a save-file line, so
     * that a {@code |} the user actually typed can never be mistaken for
     * the {@code " | "} separator between fields. Every backslash is
     * doubled first, then every {@code |} is prefixed with a backslash;
     * doing backslashes first stops the pipe-escaping step from being
     * escaped a second time.
     *
     * @param text the raw field text (e.g. a description or date)
     * @return the text with {@code \} and {@code |} escaped
     */
    public static String escape(String text) {
        return text.replace("\\", "\\\\").replace("|", "\\|");
    }

    /**
     * Reverses {@link #escape(String)}, restoring a save-file field to the
     * original text the user typed.
     *
     * @param text an escaped field read from the save file
     * @return the original, unescaped text
     * @throws IllegalArgumentException if the text has a trailing or otherwise malformed escape sequence
     */
    public static String unescape(String text) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c != '\\') {
                result.append(c);
                continue;
            }

            if (i + 1 >= text.length()) {
                throw new IllegalArgumentException("Malformed escape sequence in: " + text);
            }

            char escapedCharacter = text.charAt(i + 1);
            boolean isValidEscape = escapedCharacter == '\\' || escapedCharacter == '|';
            if (!isValidEscape) {
                throw new IllegalArgumentException("Malformed escape sequence in: " + text);
            }

            result.append(escapedCharacter);
            i++;
        }
        return result.toString();
    }
}
