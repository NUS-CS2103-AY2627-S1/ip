package nano.command;

import nano.NanoException;
import nano.Storage;
import nano.task.Task;
import nano.task.TaskList;

/**
 * A command that adds a task to the task list.
 */
public class AddCommand implements Command {
    private final Task task;

    /**
     * Creates an add command for the given task.
     *
     * @param task task to add
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    @Override
    public String execute(TaskList tasks, Storage storage) throws NanoException {
        assert task != null;
        assert tasks != null;
        assert storage != null;

        tasks.add(task);
        storage.save(tasks);

        return "Got it. Adding " + task + "\n"
                + "Now you have " + tasks.size() + " tasks in the list.";
    }
}
