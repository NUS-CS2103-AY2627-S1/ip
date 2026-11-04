package soar.task;

import soar.storage.Storage;

/**
 * Represents a task without an associated date or time.
 */
public class ToDo extends Task {
    /**
     * Creates an incomplete todo with the given description.
     *
     * @param description Description of what needs to be done.
     */
    public ToDo(String description) {
        super(description);
    }

    /**
     * Returns the icon used to identify a todo task.
     *
     * @return todo type icon
     */
    @Override
    public String getTypeIcon() {
        return "[T]";
    }

    /**
     * Serializes this todo for storage.
     *
     * @return escaped todo record
     */
    @Override
    public String toDataString() {
        return "T | " + getDataStatus() + " | " + Storage.escapeField(getDescription());
    }
}
