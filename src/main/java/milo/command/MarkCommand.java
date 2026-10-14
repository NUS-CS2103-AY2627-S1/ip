package milo.command;

import milo.exception.MiloException;
import milo.storage.Storage;
import milo.task.Task;
import milo.task.TaskList;
import milo.ui.Ui;

/**
 * Marks a task as done.
 */
public class MarkCommand extends TaskCommand {

    /**
     * Creates a command that marks the task at the given one-based number.
     *
     * @param taskNumber one-based number of the task to mark.
     */
    public MarkCommand(int taskNumber) {
        super(taskNumber);
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws MiloException {
        int taskIndex = getTaskIndex(tasks);
        boolean isPreviouslyDone = tasks.get(taskIndex).isDone();
        Task markedTask = tasks.mark(taskIndex);
        try {
            storage.save(tasks);
        } catch (MiloException exception) {
            if (!isPreviouslyDone) {
                tasks.unmark(taskIndex);
            }
            throw exception;
        }
        ui.showTaskMarkedHeader();
        ui.showTask(markedTask);
    }
}
