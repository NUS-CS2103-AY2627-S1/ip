package xiaozhi.task;

import java.util.Objects;

/**
 * A task with no date attached, other than a description.
 */
public class Todo extends Task {

    /**
     * Creates a todo with the given description.
     *
     * @param description What the todo is.
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String getTypeIcon() {
        return "T";
    }

    /**
     * Returns whether {@code other} is a Todo with the same description as
     * this one. Completion status is not part of the comparison, so a todo
     * and its already-done twin still count as duplicates; used by
     * {@link xiaozhi.command.AddCommand} to reject adding the same todo twice.
     *
     * @param other The object to compare against.
     * @return {@code true} if {@code other} is an equivalent Todo, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Todo)) {
            return false;
        }
        Todo that = (Todo) other;
        return description.equals(that.description);
    }

    /**
     * Returns a hash code consistent with {@link #equals(Object)}.
     *
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(getTypeIcon(), description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
