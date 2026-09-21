package ted.command;

import ted.Storage;
import ted.TedException;
import ted.Ui;
import ted.task.Task;
import ted.task.TaskList;

/**
 * Marks a task as done, or reverses that.
 * Marking and unmarking differ only in the stored flag and the wording, so one
 * class covers both rather than duplicating the lookup.
 */
public class MarkCommand extends Command {
    /** Zero-based position of the task to change. */
    private final int index;

    /** {@code true} to mark the task done, {@code false} to reverse it. */
    private final boolean isDone;

    /**
     * Creates a command that changes one task's done status.
     *
     * @param index  zero-based position of the task.
     * @param isDone {@code true} to mark it done, {@code false} to reverse it.
     */
    public MarkCommand(int index, boolean isDone) {
        this.index = index;
        this.isDone = isDone;
    }

    /**
     * Changes the task's done status, shows the result, and saves the list.
     *
     * @throws TedException if no task has that number, the task already has
     *                      that status, or the list cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws TedException {
        Task task = tasks.get(index);
        if (task.isDone() == isDone) {
            // Confirming a change that did not happen would suggest the user
            // picked the task they meant, when they may have mixed up two numbers.
            String explanation = isDone
                    ? " is already done. Doing it twice won't earn you extra credit: "
                    : " isn't done yet, so there's nothing to undo: ";
            throw new TedException("Task " + (index + 1) + explanation + task);
        }

        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }

        ui.showMarked(task, isDone);
        storage.save(tasks);
    }
}
