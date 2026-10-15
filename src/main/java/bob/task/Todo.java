package bob.task;

import bob.BobException;

/**
 * Represents a task with no date or time attached to it, for example
 * {@code borrow book}. It adds nothing to {@link Task} beyond its
 * type icon and its refusal of a date when it is edited, since there is
 * nothing more to remember about it.
 */
public class Todo extends Task {

    /**
     * The letter that stands for a todo, both in the box shown in a listing and
     * in the first field of a saved line.
     *
     * <p>Named as a constant so that {@link bob.storage.Storage Storage}, which has to recognize the
     * letter when reading a saved line back, can say {@code Todo.TYPE_ICON}
     * rather than repeating {@code "T"} and leaving two places to keep in step.
     */
    public static final String TYPE_ICON = "T";

    /**
     * Creates a todo that is not done yet.
     *
     * @param description what the user wants to do.
     */
    public Todo(String description) {
        super(description);
    }

    /** Returns {@link #TYPE_ICON}, the {@code T} that marks a todo. */
    @Override
    public String getTypeIcon() {
        return TYPE_ICON;
    }

    /**
     * Returns a todo with the new description.
     *
     * <p>A todo has no dates, so an edit giving one is refused rather than quietly
     * ignored: a user who typed a date expected something to change.
     */
    @Override
    protected Task buildEdited(String description, TaskEdit edit) throws BobException {
        if (edit.hasDueDate() || edit.hasStartOrEnd()) {
            throw new BobException("A todo has no dates to change, only its description.");
        }
        return new Todo(description);
    }
}
