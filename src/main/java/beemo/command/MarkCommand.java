package beemo.command;

import beemo.BeemoException;
import beemo.storage.Storage;
import beemo.task.Task;
import beemo.task.TaskList;
import beemo.ui.Ui;

/**
 * Marks a task as completed.
 */
public class MarkCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command targeting the task with the specified display number.
     *
     * @param taskNumber Display number of the task to mark.
     */
    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BeemoException {
        Task task = tasks.markAsDone(taskNumber);
        storage.saveTasks(tasks.asList());
        ui.showMarkedTask(task);
    }
}
