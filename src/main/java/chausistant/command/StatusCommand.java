package chausistant.command;

import java.io.IOException;

import chausistant.exception.ChausistantException;
import chausistant.storage.Storage;
import chausistant.task.Task;
import chausistant.task.TaskList;
import chausistant.ui.Ui;

/**
 * Shared behavior for commands that change a task's completion status.
 */
public abstract class StatusCommand extends NumberedTaskCommand {
    private final boolean isCompleted;

    /**
     * Creates a status-changing command for one user-entered task number.
     *
     * @param action the command keyword used in validation messages
     * @param taskNumberText the task number entered by the user
     * @param isCompleted the completion state to apply
     */
    protected StatusCommand(String action, String taskNumberText, boolean isCompleted) {
        super(action, taskNumberText);
        this.isCompleted = isCompleted;
    }

    /** Updates the selected task's status, saves the change, and displays the task. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage)
            throws ChausistantException, IOException {
        int taskIndex = getTaskIndex(tasks);
        Task task = tasks.get(taskIndex);
        boolean wasCompleted = task.isCompleted();
        if (wasCompleted == isCompleted) {
            String state = isCompleted ? "completed" : "incomplete";
            throw new ChausistantException("This task is already marked as " + state + ".");
        }
        task.setCompleted(isCompleted);

        try {
            storage.save(tasks);
        } catch (IOException error) {
            task.setCompleted(wasCompleted);
            throw error;
        }
        ui.showTaskStatus(task.printTask());
    }

}
