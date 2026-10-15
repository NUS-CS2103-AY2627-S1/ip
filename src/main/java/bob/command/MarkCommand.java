package bob.command;

import bob.exception.BobException;
import bob.storage.Storage;
import bob.task.Task;
import bob.task.TaskList;
import bob.ui.Ui;

/**
 * Represents a command to mark or unmark a task in the task list.
 */
public class MarkCommand extends Command {
    private final int taskId;
    private final boolean isDone;

    /**
     * Constructs a MarkCommand with the specified 1-based task ID and target
     * completion state.
     *
     * @param taskId the 1-based index of the task to mark/unmark
     * @param isDone true to mark the task as done, false to mark as not done
     */
    public MarkCommand(int taskId, boolean isDone) {
        this.taskId = taskId;
        this.isDone = isDone;
    }

    /**
     * Executes the mark/unmark command by updating the completion status of the
     * task
     * at the specified index, persisting the change to storage, and displaying
     * confirmation.
     *
     * @param tasks   the list of tasks
     * @param ui      the user interface handler
     * @param storage the storage handler to persist the task list
     * @throws BobException if the taskId is out of bounds or saving fails
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage<TaskList> storage) throws BobException {
        assertExecutionDependencies(tasks, ui, storage);
        try {
            Task task = tasks.get(taskId - 1);
            if (isDone) {
                task.mark();
            } else {
                task.unmark();
            }
            storage.save(tasks);
            ui.setTaskMarked(task, isDone);
        } catch (IndexOutOfBoundsException e) {
            throw new BobException("Error: taskId out of bounds");
        }
    }
}
