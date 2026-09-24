package hampster.command;

import hampster.exception.HampsterException;
import hampster.task.TaskList;
import hampster.ui.Ui;

/** Command that removes a task from the task list. */
public class DeleteCommand extends Command {
    private final int taskNumber;

    /** Creates a delete command for the requested task number. */
    public DeleteCommand(String[] parts) throws HampsterException {
        taskNumber = parseTaskNumber(parts, "Delete");
    }

    @Override
    public void execute(TaskList tasks, Ui ui) throws HampsterException {
        if (taskNumber > tasks.size()) {
            throw new HampsterException("Task " + taskNumber + " has escaped my lair.");
        }

        String deletedTask = tasks.get(taskNumber - 1).toString();
        tasks.remove(taskNumber - 1);

        ui.showMessage("\tExcellent. This task has been banished from the board:");
        ui.showMessage("\t  " + deletedTask);
        ui.showMessage("\tMy empire now contains " + tasks.size() + " tasks.");

    }
}
