package longfrog.task;

/** Represents a task without a date or time. */
public class Todo extends Task {
    /**
     * Creates a todo task with the given description.
     *
     * @param name the task description
     */
    public Todo(String name) {
        super(name);
    }

    /**
     * Returns this todo in its console display format.
     *
     * @return the formatted todo
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    /**
     * Returns this todo in the format used by the save file.
     *
     * @return the serialized todo
     */
    @Override
    public String toFileFormat() {
        return "T | " + (isDone ? "1" : "0") + " | " + this.name;
    }
}
