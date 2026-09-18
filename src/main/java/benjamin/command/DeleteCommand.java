package benjamin.command;

import benjamin.BenjaminException;
import benjamin.storage.Storage;
import benjamin.task.Task;
import benjamin.task.TaskList;
import benjamin.ui.Ui;

/** Removes one task from the list. */
public class DeleteCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that will remove the given task.
     *
     * @param taskNumber the one based position the user typed, checked when
     *     the command runs.
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Removes the chosen task, reports it, and saves the shorter list.
     *
     * @throws BenjaminException if there is no such task, or it cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BenjaminException {
        int index = toIndex(taskNumber, "delete", tasks);

        Task removedTask = tasks.remove(index);
        ui.showRemoved(removedTask, tasks.size());
        storage.save(tasks);
    }
}
