package nova.command;

import nova.NovaException;
import nova.Storage;
import nova.TaskList;
import nova.Ui;
import nova.task.Task;

/** Removes a task from the list. */
public class DeleteCommand extends Command {
    private final int index;

    /**
     * Creates a command that will delete one task.
     *
     * @param index zero-based position of the task
     */
    public DeleteCommand(int index) {
        this.index = index;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws NovaException {
        tasks.checkIndexInRange(index);
        Task removed = tasks.remove(index);
        storage.save(tasks.asList());
        ui.showRemoved(removed, tasks.size());
    }
}
