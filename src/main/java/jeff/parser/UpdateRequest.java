package jeff.parser;

import jeff.task.UpdateField;

/**
 * Stores the parsed details of an update command.
 */
public class UpdateRequest {
    private final int taskNumber;
    private final UpdateField field;
    private final String value;

    /**
     * Creates an update request with the specified details.
     *
     * @param taskNumber One-based task number.
     * @param field Task detail to update.
     * @param value Replacement value.
     */
    public UpdateRequest(int taskNumber, UpdateField field, String value) {
        this.taskNumber = taskNumber;
        this.field = field;
        this.value = value;
    }

    /**
     * Returns the one-based number of the task to update.
     *
     * @return One-based task number.
     */
    public int getTaskNumber() {
        return taskNumber;
    }

    /**
     * Returns the task detail to update.
     *
     * @return Task detail to update.
     */
    public UpdateField getField() {
        return field;
    }

    /**
     * Returns the replacement value.
     *
     * @return Replacement value.
     */
    public String getValue() {
        return value;
    }
}
