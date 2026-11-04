package soar.command;

import java.util.Objects;

import soar.exception.StorageException;
import soar.storage.Storage;
import soar.task.Task;
import soar.task.TaskList;
import soar.ui.Ui;

/**
 * Adds one parsed task and persists the updated task list.
 */
public class AddCommand extends Command {
    /** Task that should be added when this command executes. */
    private final Task task;

    /**
     * Creates an addition command for an already parsed task.
     *
     * @param task Validated task to add.
     */
    public AddCommand(Task task) {
        this.task = Objects.requireNonNull(task, "Task to add must not be null");
    }

    /**
     * Adds the task, persists the updated list, and reports the addition.
     *
     * @param tasks task list to update
     * @param ui interface used to show the confirmation
     * @param storage storage used to persist the updated list
     * @throws StorageException if the updated list cannot be saved
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws StorageException {
        int originalTaskCount = tasks.size();
        tasks.add(task);
        assert tasks.size() == originalTaskCount + 1
                : "Adding a task should increase the task count by one";
        assert tasks.get(originalTaskCount) == task
                : "A newly added task should be appended to the list";
        saveChange(storage, tasks, () -> {
            Task rolledBackTask = tasks.delete(tasks.size() - 1);
            assert rolledBackTask == task : "Addition rollback should remove the newly added task";
            assert tasks.size() == originalTaskCount
                    : "Addition rollback should restore the original task count";
        });
        ui.showTaskAdded(task, tasks.size());
    }
}
