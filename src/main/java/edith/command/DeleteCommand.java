package edith.command;

import edith.EdithException;
import edith.Storage;
import edith.Ui;
import edith.task.Task;
import edith.task.TaskList;

/** Deletes a task and persists the updated list. */
public class DeleteCommand extends Command {
    private final int index;
    /**
     * Creates a delete command for a one-based task number.
     *
     * @param taskNumber one-based number of the task to delete
     */
    public DeleteCommand(int taskNumber) {
        index = taskNumber - 1;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws EdithException {
        validateTaskIndex(tasks);
        Task removed = tasks.remove(index);
        try {
            storage.save(tasks);
        } catch (EdithException error) {
            tasks.add(index, removed);
            throw error;
        }
        ui.showDeletedTask(removed, tasks.size());
    }

    /** Rejects a task number that does not identify a task in the current list. */
    private void validateTaskIndex(TaskList tasks) throws EdithException {
        if (index < 0 || index >= tasks.size()) {
            throw new EdithException("OOPS!!! That task number does not exist.");
        }
    }
}
