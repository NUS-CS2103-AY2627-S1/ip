package quackers;

/**
 * Represents a short piece of information the user wants to keep.
 *
 * <p>Notes share the task list with to-dos, deadlines, and events so that users can list,
 * find, and delete all their saved information in one place.
 */
public class Note extends Task {

    /**
     * Creates an incomplete note.
     *
     * @param description the information to remember
     */
    public Note(String description) {
        super(TaskType.NOTE, description);
    }

    /**
     * Returns whether this note has a completion status.
     *
     * @return {@code false} because a note is information, not an action item
     */
    @Override
    public boolean supportsCompletionStatus() {
        return false;
    }

    /**
     * Formats this note without a completion-status marker.
     *
     * @return the note marker and text
     */
    @Override
    public String toString() {
        return "[" + getType().getSymbol() + "] " + getDescription();
    }
}
