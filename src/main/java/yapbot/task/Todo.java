package yapbot.task;

/**
 * Represents a todo task, i.e. a task with no date/time attached.
 */
public class Todo extends Task {

    /**
     * Creates a todo task with the given description.
     *
     * @param description the task description.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns this todo serialized for the save file, with its priority (if
     * any) appended as the final field.
     *
     * @return the file-format representation of this task.
     */
    @Override
    public String toFileFormat() {
        return appendPriorityField(super.toFileFormat());
    }

    /**
     * Returns this todo as it should be displayed to the user, e.g. {@code "[T][X]read book"}.
     *
     * @return the display representation of this task.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}