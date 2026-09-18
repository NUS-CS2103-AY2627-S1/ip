package alfred.command;

import alfred.AlfredException;
import alfred.Storage;
import alfred.Ui;
import alfred.task.Task;
import alfred.task.TaskList;

/**
 * Drops one task from the list.
 */
public class DeleteCommand extends Command {
    private final int index;

    /**
     * Creates a command that will remove one task.
     *
     * @param index which task, counting from 0.
     */
    public DeleteCommand(int index) {
        this.index = index;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws AlfredException {
        Task removed = tasks.delete(index);
        ui.showRemoved(removed, tasks.size());
        storage.save(tasks.toList());
    }
}
