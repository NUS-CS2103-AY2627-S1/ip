package yachiyo.command;

import static yachiyo.exception.ErrorCategory.INVALID_OPERATION;

import java.util.ArrayList;
import java.util.List;

import yachiyo.exception.YachiyoException;
import yachiyo.storage.Storage;
import yachiyo.task.Task;
import yachiyo.task.TaskList;
import yachiyo.ui.Ui;

/**
 * Deletes a selected task and saves the updated list.
 */
public class DeleteCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that deletes the task with the supplied number.
     *
     * @param taskNumber one-based number of the task to delete.
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Deletes the selected task, persists the updated list, and displays confirmation.
     *
     * @param tasks task list from which to delete.
     * @param ui user interface used to display confirmation.
     * @param storage storage used to persist the updated list.
     * @throws YachiyoException if no task can be selected or the list cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws YachiyoException {
        if (tasks.isEmpty()) {
            throw new YachiyoException(INVALID_OPERATION,
                    "There are no tasks to delete just yet. Let's add one first!"
            );
        }

        Task task = tasks.get(taskNumber);
        List<Task> updatedTasks = new ArrayList<>(tasks.getTasks());
        updatedTasks.remove(taskNumber - 1);

        storage.saveTasks(updatedTasks);
        tasks.delete(taskNumber);
        ui.showTaskDeleted(task, tasks.size());
    }
}
