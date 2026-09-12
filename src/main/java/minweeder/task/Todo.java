package minweeder.task;

import java.util.Objects;

/**
 * A simple task with no associated date or time.
 */
public class Todo extends Task {
    /**
     * Creates a todo with the given description.
     *
     * @param description the description of the todo.
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String toFileString() {
        return "T | " + super.toFileFields();
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Todo)) {
            return false;
        }
        return this.getDescription().equals(((Todo) other).getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(Todo.class, this.getDescription());
    }
}
