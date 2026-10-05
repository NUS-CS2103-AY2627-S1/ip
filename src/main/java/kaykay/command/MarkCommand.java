package kaykay.command;

/**
 * Marks a task as done.
 */
public final class MarkCommand extends ChangeStatusCommand {
    /**
     * Creates a mark command for a task number.
     *
     * @param taskNumber one-based number of the task to mark.
     */
    public MarkCommand(String taskNumber) {
        super(taskNumber, true);
    }
}
