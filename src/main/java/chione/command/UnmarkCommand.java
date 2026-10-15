package chione.command;

import chione.ChioneException;
import chione.Storage;
import chione.Ui;
import chione.task.Task;
import chione.task.TaskList;

/**
 * Marks a task as not done, undoing a {@link MarkCommand}.
 */
public class UnmarkCommand extends Command {
    /** Zero-based position of the task to unmark. */
    private final int index;

    /**
     * Remembers which task to unmark.
     *
     * @param index zero-based position of the task; whether the list actually
     *              holds that position is decided by {@link TaskList} when this
     *              command is carried out
     */
    public UnmarkCommand(int index) {
        this.index = index;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ChioneException {
        Task task = tasks.get(index);
        task.markAsNotDone();
        ui.showUnmarked(task);
        storage.save(tasks);
    }
}
