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
 * Marks a selected task as incomplete and saves the updated list.
 */
public class UnmarkCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that unmarks the task with the supplied number.
     *
     * @param taskNumber one-based number of the task to unmark.
     */
    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Unmarks the selected task and displays its updated completion state.
     *
     * @param tasks task list containing the selected task.
     * @param ui user interface used to display the result.
     * @param storage storage used to persist the updated list.
     * @throws YachiyoException if no task can be selected or the list cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws YachiyoException {
        if (tasks.isEmpty()) {
            throw new YachiyoException(INVALID_OPERATION,
                    "There are no tasks to unmark just yet. Let's add one first!"
            );
        }

        Task task = tasks.get(taskNumber);
        if (!task.isCompleted()) {
            ui.showAlreadyUnmarked(task);
            return;
        }

        List<Task> updatedTasks = new ArrayList<>(tasks.getTasks());
        updatedTasks.set(taskNumber - 1, task.withCompletionStatus(false));

        storage.saveTasks(updatedTasks);
        task.markAsNotDone();
        ui.showTaskUnmarked(task, tasks.getRemainingTaskCount());
    }
}
