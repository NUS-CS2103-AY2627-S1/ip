package dingleberry.command;

import dingleberry.exception.DingleberryException;
import dingleberry.model.Task;
import dingleberry.model.TaskList;
import dingleberry.persistence.Storage;
import dingleberry.ui.Ui;

/** Adds an already-constructed task (todo, deadline, or event) to the list. */
public class AddCommand extends Command {
    /** Stores the task to append when this command executes. */
    private final Task taskToAdd;

    /**
     * Creates a command that adds the given task.
     *
     * @param task the task to append to the list.
     */
    public AddCommand(final Task task) {
        this.taskToAdd = task;
    }

    @Override
    public final void execute(final TaskList tasks, final Ui ui,
                      final Storage storage)
            throws DingleberryException {
        assert taskToAdd != null : "An add command must contain a task.";
        if (tasks.containsEquivalentTask(taskToAdd)) {
            throw new DingleberryException("That task already exists.");
        }
        tasks.add(taskToAdd);
        ui.showTaskAdded(taskToAdd, tasks.size());
        saveTasks(tasks, storage, ui);
    }
}
