package dobby.command;

import dobby.logic.DobbyLogic;

/**
 * Removes a specified task from Dobby's task list.
 */
public final class DeleteCommand extends Command {
    /** One-based number of the task to remove. */
    private final int taskNumber;

    /**
     * Creates a command for one task number.
     *
     * @param taskNumber one-based task number
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /** Removes the selected task. */
    @Override
    public String execute(DobbyLogic logic) {
        return logic.deleteTask(taskNumber);
    }
}
