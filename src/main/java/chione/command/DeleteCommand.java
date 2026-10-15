package chione.command;

import chione.ChioneException;
import chione.Storage;
import chione.Ui;
import chione.task.Task;
import chione.task.TaskList;

/**
 * Removes a task from the list.
 */
public class DeleteCommand extends Command {
    /** Zero-based position of the task to remove. */
    private final int index;

    /**
     * Remembers which task to remove.
     *
     * @param index zero-based position of the task; whether the list actually
     *              holds that position is decided by {@link TaskList} when this
     *              command is carried out
     */
    public DeleteCommand(int index) {
        this.index = index;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ChioneException {
        // remove() both takes the task out and hands it back, so it can still be
        // shown to the user after it has left the list.
        Task removed = tasks.remove(index);
        ui.showRemoved(removed, tasks.size());
        storage.save(tasks);
    }
}
