package yourhand.commands;

import yourhand.exceptions.YourHandException;
import yourhand.storage.Storage;
import yourhand.tasks.Task;
import yourhand.tasks.TaskList;
import yourhand.ui.Ui;

/** Marks or unmarks one task by its one-based task number. */
public class TaskStatusCommand extends Command {
    private final int taskNumber;
    private final boolean isMarkCommand;

    /** Creates a command that marks or unmarks the specified task. */
    public TaskStatusCommand(int taskNumber, boolean isMarkCommand) {
        this.taskNumber = taskNumber;
        this.isMarkCommand = isMarkCommand;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage) throws YourHandException {
        Task task = taskList.getTask(taskNumber);
        boolean wasUpdated = isMarkCommand ? task.markAsDone() : task.markAsUndone();
        if (wasUpdated) {
            try {
                saveTasks(taskList, storage);
            } catch (YourHandException exception) {
                if (isMarkCommand) {
                    task.markAsUndone();
                } else {
                    task.markAsDone();
                }
                throw exception;
            }
        }
        ui.showTaskStatus(task, isMarkCommand, wasUpdated);
    }
}
