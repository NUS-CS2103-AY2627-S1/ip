package kaykay.command;

/**
 * Marks a task as not done.
 */
public final class UnmarkCommand extends ChangeStatusCommand {
    /**
     * Creates an unmark command for a task number.
     *
     * @param taskNumber one-based number of the task to unmark.
     */
    public UnmarkCommand(String taskNumber) {
        super(taskNumber, false);
    }
}
