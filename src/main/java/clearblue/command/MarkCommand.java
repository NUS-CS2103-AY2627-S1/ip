package clearblue.command;

import clearblue.ClearblueException;
import clearblue.storage.Storage;
import clearblue.task.Task;
import clearblue.task.TaskList;
import clearblue.ui.Ui;

/**
 * Marks or unmarks a task by its one-based list number, reports it to the
 * user, and saves the list.
 */
public class MarkCommand extends Command {
    private final int taskNumber;
    private final boolean isDone;

    private int taskIndex;

    /**
     * Creates a command that marks or unmarks the given task number.
     *
     * @param taskNumber one-based task number
     * @param isDone {@code true} to mark the task as done, {@code false} to unmark it
     */
    public MarkCommand(int taskNumber, boolean isDone) {
        this.taskNumber = taskNumber;
        this.isDone = isDone;
    }

    /**
     * Marks or unmarks the task at this command's task number, reports it
     * through {@code ui}, and saves the updated list via {@code storage}.
     *
     * @param tasks task list containing the target task
     * @param ui user interface to report through
     * @param storage storage to persist the change through
     * @throws ClearblueException if the task number is invalid, or saving fails
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ClearblueException {
        String action = isDone ? "mark" : "unmark";
        taskIndex = requireValidIndex(tasks, taskNumber, action);

        Task task = tasks.get(taskIndex);
        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        ui.showTaskStatusChanged(task, isDone);
        storage.save(tasks.asList());
    }

    /**
     * {@inheritDoc}
     *
     * @return {@code true}
     */
    @Override
    public boolean isUndoable() {
        return true;
    }

    /**
     * Reverts the task this command marked or unmarked back to its
     * previous status.
     *
     * @param tasks task list containing the target task
     * @param ui user interface to report through
     * @param storage storage to persist the change through
     * @throws ClearblueException if saving fails
     */
    @Override
    public void undo(TaskList tasks, Ui ui, Storage storage) throws ClearblueException {
        Task task = tasks.get(taskIndex);
        if (isDone) {
            task.markAsNotDone();
        } else {
            task.markAsDone();
        }
        ui.showTaskStatusChanged(task, !isDone);
        storage.save(tasks.asList());
    }
}
