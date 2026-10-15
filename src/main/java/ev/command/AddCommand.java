package ev.command;

import ev.EvException;
import ev.storage.Storage;
import ev.task.Task;
import ev.task.TaskList;
import ev.ui.Ui;

/**
 * Adds one ready-made task to the end of the list.
 */
public class AddCommand extends Command {

    private final Task task;

    /**
     * Creates a command that will add the given task.
     *
     * @param task the task the parser built from the user's input.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws EvException {
        tasks.add(task);
        ui.showAdded(task, tasks);
        storage.save(tasks.getTasks());
    }
}
