package habpyduck.task;

import habpyduck.storage.Storage;

/**
 * Represents a task without any date or time attached to it.
 */
public class Todo extends Task {
    /**
     * Creates a todo task with the given description.
     *
     * @param description the text that describes the task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns this todo in the simple text format used when saving tasks.
     *
     * @return a line of text that can be written to the save file.
     */
    @Override
    public String toFileString() {
        return Storage.TODO_TASK_TYPE + Storage.FILE_FIELD_SEPARATOR + getDoneStatus()
                + Storage.FILE_FIELD_SEPARATOR + Storage.escapeFileField(description)
                + formatTagsForFile();
    }

    /**
     * Returns the todo task in the format shown to users.
     *
     * @return the task type, status icon, and description.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
