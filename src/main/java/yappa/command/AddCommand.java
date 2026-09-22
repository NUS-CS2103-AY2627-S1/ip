package yappa.command;

import yappa.exception.YappaException;
import yappa.task.Task;
import yappa.task.TaskList;
import yappa.ui.Ui;

/** Adds a task to the task list. */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates an add command for the specified task.
     *
     * @param task Task to add.
     */
    public AddCommand(Task task) {
        // Commands should never be created without a task to add.
        assert task != null;
        this.task = task;
    }

    /**
     * Adds the task and formats the result.
     *
     * @param tasks Task list to update.
     * @param ui User interface used to format the response.
     * @return Formatted addition response.
     * @throws YappaException If the task cannot be added.
     */
    @Override
    public String execute(TaskList tasks, Ui ui) throws YappaException {

        tasks.add(task);

        return ui.showTaskAdded(task, tasks.size());
    }

    /**
     * Returns whether this command changes the task list.
     *
     * @return True because the command adds a task.
     */
    @Override
    public boolean modifiesTasks() {
        return true;
    }
}
