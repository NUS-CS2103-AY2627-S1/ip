package charlie.task;

import charlie.command.UpdateField;
import charlie.exception.CharlieException;

/**
 * Represents a task without an associated date or time.
 */
public class Todo extends Task {

    /**
     * Creates a todo task with its description and completion status.
     *
     * @param description Description of the task.
     * @param isDone Whether the task is completed.
     */
    public Todo(String description, boolean isDone) {
        super(description, isDone);
    }

    /**
     * Creates a copy of this todo with the requested completion status.
     *
     * @param isDone Completion status for the copy.
     * @return Copied todo with the requested status.
     */
    @Override
    public Task copyWithStatus(boolean isDone) {
        return new Todo(description, isDone);
    }

    /**
     * Returns the display representation of this todo.
     *
     * @return Todo type, completion status, and description.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    /**
     * Converts this todo into the format used in the save file.
     *
     * @return Serialized todo data.
     */
    @Override
    public String saveFileFormat() {
        String status = isDone ? "Done" : "Not done";
        return "T" + " | " + status + " | " + this.description;
    }

    /**
     * Creates a copy of this todo with the requested field replaced.
     *
     * @param updateField Field to replace.
     * @param newValue Replacement value for the field.
     * @return Updated copy of this todo.
     * @throws CharlieException If the requested field does not belong to a todo.
     */
    @Override
    public Task createUpdatedTask(UpdateField updateField, String newValue) {
        return switch (updateField) {
            case DESCRIPTION -> createTaskWithDescription(newValue);
            case DEADLINE, FROM, TO -> throw new CharlieException(
                    "A todo only has a description field.");
        };
    }

    /**
     * Creates a copy of this todo with a replacement description.
     *
     * @param newValue Replacement description.
     * @return Updated todo copy.
     */
    private Todo createTaskWithDescription(String newValue) {
        return new Todo(newValue, isDone);
    }
}
