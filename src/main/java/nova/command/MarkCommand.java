package nova.command;

import nova.NovaException;
import nova.Storage;
import nova.TaskList;
import nova.Ui;
import nova.task.Task;

/** Marks a task as done, or as not done yet. */
public class MarkCommand extends Command {
    private final int index;
    private final boolean isDone;

    /**
     * Creates a command that will change the done state of one task.
     *
     * @param index  zero-based position of the task
     * @param isDone true to mark the task done, false to mark it not done
     */
    public MarkCommand(int index, boolean isDone) {
        this.index = index;
        this.isDone = isDone;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws NovaException {
        tasks.checkIndexInRange(index);
        Task task = tasks.get(index);

        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        storage.save(tasks.asList());

        if (isDone) {
            ui.showMarked(task);
        } else {
            ui.showUnmarked(task);
        }
    }
}
