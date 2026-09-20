package edith.command;

import edith.EdithException;
import edith.Storage;
import edith.Ui;
import edith.task.Task;
import edith.task.TaskList;

/** Changes a task's completion status. */
public class MarkCommand extends Command {
    private final int index;
    private final boolean done;
    /**
     * Creates a command for a one-based task number.
     *
     * @param taskNumber one-based number of the task to update
     * @param done whether to mark the task as complete
     */
    public MarkCommand(int taskNumber, boolean done) {
        index = taskNumber - 1;
        this.done = done;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws EdithException {
        validateTaskIndex(tasks);
        Task task = tasks.get(index);
        boolean wasDone = task.isDone();
        if (done) {
            task.markAsDone();
        } else {
            task.unmarkAsDone();
        }
        try {
            storage.save(tasks);
        } catch (EdithException error) {
            if (wasDone) {
                task.markAsDone();
            } else {
                task.unmarkAsDone();
            }
            throw error;
        }
        if (done) {
            ui.showMarked(task);
        } else {
            ui.showUnmarked(task);
        }
    }

    /** Rejects a task number that does not identify a task in the current list. */
    private void validateTaskIndex(TaskList tasks) throws EdithException {
        if (index < 0 || index >= tasks.size()) {
            throw new EdithException("OOPS!!! That task number does not exist.");
        }
    }
}
