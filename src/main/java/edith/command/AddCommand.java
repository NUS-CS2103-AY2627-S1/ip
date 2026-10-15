package edith.command;

import edith.exception.EdithException;
import edith.task.Task;
import edith.task.TaskList;
import edith.ui.Ui;

/** Adds one parsed task to Edith's task list. */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command that adds the supplied task.
     *
     * @param task the fully parsed task to add
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Adds the task, saves the updated list, and displays a confirmation.
     *
     * @param tasks the task list to which the task is added
     * @param ui the user interface used to display the confirmation
     * @throws EdithException if the updated task list cannot be saved
     */
    @Override
    public void execute(TaskList tasks, Ui ui) throws EdithException {
        if (tasks.containsEquivalent(task)) {
            throw new EdithException("That task already exists in your list.");
        }
        int previousSize = tasks.size();
        tasks.add(task);
        assert tasks.size() == previousSize + 1 && tasks.get(previousSize) == task
                : "Adding a task must append exactly that task";
        saveTasks(tasks, () -> tasks.remove(previousSize));
        ui.showTaskAdded(task, tasks.size());
    }
}
