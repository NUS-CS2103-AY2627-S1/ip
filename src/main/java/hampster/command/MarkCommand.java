package hampster.command;

import hampster.exception.HampsterException;
import hampster.task.TaskList;
import hampster.ui.Ui;

/** Command that toggles the completion status of a task. */
public class MarkCommand extends Command {
    private final int taskNumber;

    /** Creates a mark command for the requested task number. */
    public MarkCommand(String[] parts) throws HampsterException {
        taskNumber = parseTaskNumber(parts, "Mark");
    }

    @Override
    public void execute(TaskList tasks, Ui ui) throws HampsterException {
        assert tasks != null : "Task list must not be null";
        assert taskNumber >= 1 : "Task numbers are 1-based";
        assert taskNumber <= tasks.size() : "Task number must be within the current list";

        if (taskNumber > tasks.size()) {
            throw new HampsterException("Task " + taskNumber + " has escaped my lair.");
        }

        if (tasks.get(taskNumber - 1).toggleState()) {
            ui.showMessage("\tExcellent. Task " + taskNumber + " has been conquered.");
        } else {
            ui.showMessage("\tCurses! Task " + taskNumber + " has escaped. Recapture it at once.");
        }

        ui.showMessage("\t" + tasks.get(taskNumber - 1));

    }
}
