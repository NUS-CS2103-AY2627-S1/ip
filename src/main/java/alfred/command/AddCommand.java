package alfred.command;

import alfred.exception.AlfredException;
import alfred.storage.Storage;
import alfred.task.Task;
import alfred.task.TaskList;
import alfred.ui.Ui;

/**
 * Adds a task to the task list.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command that adds the given task.
     *
     * @param task task to add
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws AlfredException {
        if (tasks.containsEquivalentTask(task)) {
            throw new AlfredException("Alfred already has an identical mission in the cave ledger.");
        }
        tasks.add(task);
        storage.save(tasks);
        ui.showTaskAdded(task, tasks.size());
    }
}
