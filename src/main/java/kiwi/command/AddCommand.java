package kiwi.command;

import kiwi.KiwiException;
import kiwi.Storage;
import kiwi.Ui;
import kiwi.task.Task;
import kiwi.task.TaskList;

/**
 * Adds a task (to-do, deadline, or event) to the list and saves.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command that adds the given task.
     *
     * @param task the task to add (already parsed).
     */
    public AddCommand(Task task) {
        assert task != null : "Parser always builds a Task before wrapping it in AddCommand";
        this.task = task;
    }

    /**
     * Adds the task if it is not a duplicate, saves the list, and shows a confirmation.
     *
     * {@inheritDoc}
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws KiwiException {
        if (tasks.containsSameDetails(task)) {
            throw new KiwiException("That task is already in your list.");
        }
        tasks.add(task);
        saveOrRollback(tasks, storage, () -> tasks.delete(tasks.size() - 1));
        ui.showTaskAdded(task, tasks.size());
    }
}
