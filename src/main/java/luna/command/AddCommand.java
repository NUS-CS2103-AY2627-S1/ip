package luna.command;

import luna.exception.LunaException;
import luna.storage.Storage;
import luna.task.Task;
import luna.task.TaskList;

/**
 * Adds a task created from command arguments.
 */
public abstract class AddCommand extends Command {
    private final TaskList tasks;
    private final Storage storage;

    /**
     * Creates an add command.
     *
     * @param tasks Task list to update.
     * @param storage Storage to update.
     */
    protected AddCommand(TaskList tasks, Storage storage) {
        this.tasks = tasks;
        this.storage = storage;
    }

    @Override
    public final String execute() throws LunaException {
        Task task = createTask();
        tasks.add(task);
        storage.save(tasks.getUnmodifiableList());

        return "Got it. I've added this task:\n" + task
                + "\nNow you have " + tasks.size() + " tasks in the list.";
    }

    /**
     * Creates the task represented by this command.
     *
     * @return Task to add.
     * @throws LunaException If the command arguments are invalid.
     */
    protected abstract Task createTask() throws LunaException;
}
