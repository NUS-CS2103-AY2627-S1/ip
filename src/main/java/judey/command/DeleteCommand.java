package judey.command;

import judey.exception.JudeyException;
import judey.storage.Storage;
import judey.task.Task;
import judey.tasklist.TaskList;
import judey.ui.Ui;

/**
 * Represents deleting a task
 */
public class DeleteCommand extends Command {
    private final int index;

    /**
     * Creates a Delete command
     * @param index task at this index in the list would be deleted through execute
     */
    public DeleteCommand(int index) {
        this.index = index;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws JudeyException {
        Task removedTask = tasks.delete(index);
        storage.save(tasks.getTasks());
        ui.showTaskDeleted(removedTask, tasks.getSize());
    }
}
