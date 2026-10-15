package dennis.task;

import java.util.Objects;

import dennis.DennisException;

/**
 * A task with nothing but a description; no date attached.
 */
public class Todo extends Task {
    /**
     * Creates a todo with the given description.
     *
     * @param description the task text
     * @throws DennisException if {@code description} is blank or contains the
     *                         {@code '|'} save-file separator
     */
    public Todo(String description) throws DennisException {
        super(validateDescription(description));
    }

    /**
     * Checks the description and returns it unchanged when acceptable.
     *
     * @throws DennisException if it is blank or contains a {@code '|'}
     */
    private static String validateDescription(String description)
            throws DennisException {
        if (description.isBlank()) {
            throw new DennisException(
                    "I'm sorry, todo must contain a task.");
        }

        return rejectSeparator(description, "A task description");
    }

    @Override
    public String toFileFormat() {
        return "T | " + getStatusNumber() + " | " + description;
    }

    /**
     * Two todos are equal when they have the same description; completion
     * status does not count, so this is what {@link TaskList#add} uses to
     * reject an exact duplicate.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Todo other)) {
            return false;
        }
        return description.equals(other.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Todo.class, description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
