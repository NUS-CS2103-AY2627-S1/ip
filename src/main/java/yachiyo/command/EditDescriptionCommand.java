package yachiyo.command;

import yachiyo.task.Task;

/**
 * Replaces the description of a selected task.
 */
public class EditDescriptionCommand extends EditCommand {
    private final String description;

    /**
     * Creates a command that replaces the description of a selected task.
     *
     * @param taskNumber one-based number of the task to edit.
     * @param description replacement task description.
     */
    public EditDescriptionCommand(int taskNumber, String description) {
        super(taskNumber);
        assert description != null : "Replacement description must not be null";
        assert !description.isBlank() : "Replacement description must not be blank";

        this.description = description;
    }

    @Override
    protected Task createEditedTask(Task task) {
        return task.withDescription(description);
    }
}
