package matthew.command;

import matthew.exception.MatthewException;
import matthew.storage.Storage;
import matthew.task.Task;
import matthew.task.TaskList;
import matthew.ui.Ui;

/** Marks a task as done and saves the updated task list. */
public class MarkCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that marks the task with the specified number.
     *
     * @param taskNumber One-based number of the task to mark.
     */
    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Marks the task, persists the task list, and displays a confirmation.
     *
     * @param tasks Task list to modify.
     * @param ui User interface used to display the result.
     * @param storage Storage used to persist the updated list.
     * @throws MatthewException If the task number is invalid or the list cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage)
            throws MatthewException {
        Task task = tasks.mark(taskNumber);
        storage.save(tasks);
        ui.showMarkedTask(task);
    }
}
