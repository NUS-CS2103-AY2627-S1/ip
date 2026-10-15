package chione.command;

import chione.ChioneException;
import chione.Storage;
import chione.Ui;
import chione.task.Task;
import chione.task.TaskList;

/**
 * Marks a task as done.
 */
public class MarkCommand extends Command {
    /** Zero-based position of the task to mark. */
    private final int index;

    /**
     * Remembers which task to mark.
     *
     * @param index zero-based position of the task; whether the list actually
     *              holds that position is decided by {@link TaskList} when this
     *              command is carried out
     */
    public MarkCommand(int index) {
        this.index = index;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ChioneException {
        Task task = tasks.get(index);
        task.markAsDone();
        ui.showMarked(task);
        storage.save(tasks);
    }
}
