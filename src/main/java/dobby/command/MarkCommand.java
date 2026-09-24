package dobby.command;

import dobby.logic.DobbyLogic;

/**
 * Marks a specified task as complete.
 */
public final class MarkCommand extends Command {
    /** One-based number of the task to mark. */
    private final int taskNumber;

    /**
     * Creates a command for one task number.
     *
     * @param taskNumber one-based task number
     */
    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /** Marks the selected task as complete. */
    @Override
    public String execute(DobbyLogic logic) {
        return logic.changeTaskStatus(taskNumber, true);
    }
}
