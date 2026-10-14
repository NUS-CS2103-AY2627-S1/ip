package milo.command;

import milo.exception.MiloException;
import milo.task.TaskList;

/**
 * Represents a command that operates on one task selected by its list number.
 */
abstract class TaskCommand extends Command {
    private final int taskNumber;

    /** Creates a command that targets the given one-based task number. */
    TaskCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Returns the validated zero-based index of the selected task.
     *
     * @param tasks task list containing the selected task.
     * @return zero-based index of the selected task.
     * @throws MiloException if the selected task number is outside the list.
     */
    protected int getTaskIndex(TaskList tasks) throws MiloException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new MiloException("There is no task numbered " + taskNumber + ".");
        }
        int taskIndex = taskNumber - 1;
        assert taskIndex >= 0 && taskIndex < tasks.size() : "Validated task number must map to an existing task";
        return taskIndex;
    }
}
