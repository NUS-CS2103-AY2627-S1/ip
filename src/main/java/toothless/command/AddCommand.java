package toothless.command;

import toothless.exception.ToothlessException;
import toothless.storage.Storage;
import toothless.task.Task;
import toothless.task.TaskList;
import toothless.ui.Ui;

/**
 * Provides the shared workflow for commands that create and add a task.
 */
public abstract class AddCommand extends Command {
    /**
     * Creates the shared base for a command that adds one task to Toothless's list.
     */
    public AddCommand() {
    }

    /**
     * Creates the command-specific task before it is added to the task list.
     *
     * @return task created by the concrete command
     */
    protected abstract Task createTask();

    /**
     * Adds the created task, displays it, and saves the updated task list.
     *
     * @param taskList list that receives the newly created task
     * @param ui user interface used to confirm the addition or report a save failure
     * @param storage storage used to persist the updated task list
     */
    @Override
    public final void execute(TaskList taskList, Ui ui, Storage storage) throws ToothlessException {
        Task task = createTask();
        assert task != null : "Add command should create a task before execution";
        TaskList proposedTasks = taskList.copy();
        proposedTasks.addUniqueTask(task);
        if (saveTasks(taskList, proposedTasks, ui, storage)) {
            ui.showTaskAdded(task, taskList.size());
        }
    }
}
