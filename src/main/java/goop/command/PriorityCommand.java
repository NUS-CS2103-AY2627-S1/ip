package goop.command;

import java.io.IOException;
import java.util.Objects;

import goop.exception.GoopException;
import goop.storage.Storage;
import goop.task.Priority;
import goop.task.Task;
import goop.task.TaskList;
import goop.ui.Ui;

/**
 * Represents a request to assign or clear one task's priority.
 */
public class PriorityCommand extends TaskCommand {
    private final Priority priority;

    /**
     * Creates a priority command for a one-based task number.
     *
     * @param taskNumber Task to update.
     * @param priority Priority to assign, or NONE to clear it.
     */
    public PriorityCommand(int taskNumber, Priority priority) {
        super(taskNumber);
        this.priority = Objects.requireNonNull(priority);
    }

    /**
     * Updates and saves the priority, restoring it if saving fails.
     *
     * @param tasks Current task list.
     * @param ui Interface used to display the updated task.
     * @param storage Storage used to persist the change.
     * @throws GoopException If the task number is outside the list.
     * @throws IOException If the changed list cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws GoopException, IOException {
        Task task = tasks.get(getTaskIndex(tasks, "prioritize"));
        Priority previousPriority = task.getPriority();
        task.setPriority(priority);
        try {
            storage.saveTasks(tasks);
        } catch (IOException error) {
            task.setPriority(previousPriority);
            throw error;
        }
        ui.showPriorityChanged(task);
    }
}
