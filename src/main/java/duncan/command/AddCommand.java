package duncan.command;

import duncan.DuncanException;
import duncan.Storage;
import duncan.Ui;
import duncan.task.Task;
import duncan.task.TaskList;

/**
 * Adds one already-built task (a {@link duncan.task.Todo}, {@link duncan.task.Deadline}, or
 * {@link duncan.task.Event}) to the task list, unless the list already has the same task.
 */
public class AddCommand extends Command {
    private static final String MESSAGE_DUPLICATE_TASK = "HEY! this task is already in your list";

    private final Task task;

    /**
     * Creates a command that adds the given task to the task list.
     *
     * @param task The already-built task to add.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DuncanException {
        if (tasks.hasDuplicateOf(task)) {
            throw new DuncanException(MESSAGE_DUPLICATE_TASK);
        }
        tasks.add(task);
        assert tasks.get(tasks.size() - 1) == task;
        ui.showTaskAdded(task, tasks.size());
        saveTasks(tasks, ui, storage);
    }
}
