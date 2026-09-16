package walter.command;

import walter.DukeException;
import walter.storage.Storage;
import walter.task.Task;
import walter.task.TaskList;
import walter.ui.Ui;

/**
 * Adds one already-parsed task and persists the changed task list.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates an add command for a Todo, Deadline, or Event.
     *
     * @param task Parsed task to add when the command executes.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.ADD;
    }

    /**
     * Adds the parsed task, displays the updated count, and persists the task list.
     *
     * @param tasks Task list to which the task is added.
     * @param ui User interface used to display the added task.
     * @param storage Storage used to persist the updated task list.
     * @throws DukeException If the task list is full or the updated list cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DukeException {
        tasks.add(task);
        try {
            storage.save(tasks.getTasks());
        } catch (DukeException exception) {
            tasks.delete(tasks.size() - 1);
            throw exception;
        }
        ui.showAddedTask(task, tasks.size());
    }
}
