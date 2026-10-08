package rudra.task;

import java.util.List;

/**
 * Represents a task that should be completed by a specific time.
 */
public class Deadline extends Task {
    private final TaskDateTime by;

    /**
     * Creates a deadline task with a description and due time.
     *
     * @param description Description of the task.
     * @param by Due time entered by the user.
     */
    public Deadline(String description, TaskDateTime by) {
        super(TaskType.DEADLINE, description);
        assert by != null : "A deadline must have a due date";

        this.by = by;
    }

    /**
     * Adds the deadline timestamp to the base task storage fields.
     *
     * @return Storage fields for a deadline task.
     */
    @Override
    protected List<String> getStorageFields() {
        List<String> storageFields = super.getStorageFields();
        addStorageFields(storageFields, this.by.toStorageString());
        return storageFields;
    }

    /**
     * Returns the due date and time of this deadline.
     *
     * @return Deadline due date and time.
     */
    public TaskDateTime getBy() {
        return this.by;
    }

    /**
     * Returns the user-facing text for this deadline task.
     *
     * @return Formatted deadline task description.
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + this.by.toDisplayString() + ")";
    }
}
