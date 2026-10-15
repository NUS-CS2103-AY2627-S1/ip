package nova.command;

import java.io.IOException;

import nova.exception.NovaException;
import nova.task.Task;
import nova.task.TaskList;
import nova.ui.Ui;

/**
 * Marks or unmarks a numbered task.
 */
public class MarkCommand extends Command {
    /**
     * Represents a requested change to a task's completion state.
     */
    public enum CompletionAction {
        MARK,
        UNMARK
    }

    private final int taskNumber;
    private final CompletionAction completionAction;

    /**
     * Creates a command that changes a task's completion state.
     *
     * @param taskNumber one-based number of the task to update
     * @param completionAction completion-state change to perform
     */
    public MarkCommand(int taskNumber, CompletionAction completionAction) {
        this.taskNumber = taskNumber;
        this.completionAction = completionAction;
    }

    /**
     * Changes the task's completion state, saves the task list, and displays the result.
     *
     * @param tasks task list to update
     * @param ui UI used to display the updated task
     * @throws NovaException if the task number does not exist
     * @throws IOException if the updated task list cannot be saved
     */
    @Override
    public void execute(TaskList tasks, Ui ui) throws NovaException, IOException {
        Task task;
        boolean isMarked;
        switch (completionAction) {
            case MARK:
                task = tasks.mark(taskNumber);
                isMarked = true;
                break;
            case UNMARK:
                task = tasks.unmark(taskNumber);
                isMarked = false;
                break;
            default:
                throw new AssertionError("Unexpected completion action: " + completionAction);
        }
        ui.showMarkedTask(task, isMarked);
    }
}
