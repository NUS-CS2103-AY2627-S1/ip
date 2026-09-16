package eve.command;

import eve.EveException;
import eve.Storage;
import eve.Ui;
import eve.task.Task;
import eve.task.TaskList;

/** Adds a task to the list, persists the change, and reports the addition. */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command that adds the given task.
     *
     * @param task the already-parsed task to add, e.g. from {@link Parser}.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws EveException {
        // Task.equals() compares type/description (and dates, for Deadline/Event), so this
        // catches an exact duplicate -- the same task typed in twice by mistake -- without
        // stopping the user from adding two genuinely different tasks that just sound similar.
        if (tasks.asList().contains(task)) {
            throw new EveException("Oops, that's already on your list! No need to add it twice.");
        }
        tasks.add(task);
        storage.save(tasks.asList());
        ui.showTaskAdded(task, tasks.size());
    }
}
