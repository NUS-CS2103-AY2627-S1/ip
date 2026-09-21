package answerme.task;

import java.util.Objects;

/**
 * Represents a todo task.
 */
public class ToDo extends Task {
    /**
     * Creates a todo task with the specified description.
     *
     * @param description The todo's description.
     */
    public ToDo(String description) {
        super(description);
    }

    /**
     * {@inheritDoc}
     *
     * Returns the todo's status and description with a todo marker.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    /**
     * {@inheritDoc}
     *
     * Returns the todo in storage format.
     */
    @Override
    public String toStorageFormat() {
        return getLogFormat("Todo");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof ToDo other) {
            return description.equals(other.description);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(description);
    }
}
