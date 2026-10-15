package thomas.task;

/**
 * A task with no date or time attached, such as {@code borrow book}.
 * <p>
 * It adds no state of its own to {@link Task}; it exists so that a to-do can be
 * told apart from the other task types and shown with its own {@code [T]} tag.
 */
public class TodoTask extends Task {
    /**
     * Creates a to-do that is not yet done.
     *
     * @param description The task text, without the {@code todo} keyword.
     */
    public TodoTask(String description) {
        super(description);
    }

    /**
     * Returns the to-do prefixed with its type tag.
     *
     * @return For example {@code "[T][ ] borrow book"}.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    /**
     * Returns the to-do encoded for the save file, tagged {@code T}.
     *
     * @return For example {@code "T | 0 | borrow book"}.
     */
    @Override
    public String toSaveFormat() {
        return "T" + FIELD_SEPARATOR + super.toSaveFormat();
    }
}
