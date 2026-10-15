package sage.task;

/**
 * Stores a snippet of information that has no completion state.
 */
public class Note extends Task {
    /**
     * Creates a note containing the supplied text.
     *
     * @param text The information to remember.
     */
    public Note(String text) {
        super(text, TaskType.NOTE);
    }

    /**
     * Displays the note without a task completion checkbox.
     *
     * @return The note marker followed by its text.
     */
    @Override
    public String toString() {
        return "[N] " + description;
    }
}
