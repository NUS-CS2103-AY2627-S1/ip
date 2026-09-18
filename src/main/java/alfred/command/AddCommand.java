package alfred.command;

import alfred.AlfredException;
import alfred.Storage;
import alfred.Ui;
import alfred.task.Task;
import alfred.task.TaskList;

/**
 * Stores a new task at the end of the list. One class covers {@code todo},
 * {@code deadline}, and {@code event}.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command that will store one task.
     *
     * @param task the task to add.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws AlfredException {
        tasks.add(task);
        ui.showAdded(task, tasks.size());
        storage.save(tasks.toList());
    }
}
