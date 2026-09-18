package anby.task;

/**
 * Represents a task without a date.
 */
public class Todo extends Task {
    /**
     * Creates a todo task with the given name.
     *
     * @param name todo description
     */
    public Todo(String name) {
        super(name);
    }

    /**
     * Converts this todo into the format used in the data file.
     *
     * @return file storage representation of this todo
     */
    @Override
    public String toFileString() {
        return "T | " + super.toFileString();
    }

    /**
     * Returns the user-facing representation of this todo.
     *
     * @return display string for this todo
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
