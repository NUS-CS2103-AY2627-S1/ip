package caitlyn;

import java.util.List;

/**
 * A command that creates a ToDo task.
 */
public final class TodoCommand extends Command {
    /** The description of the new task. */
    private final String description;

    /**
     * Creates a ToDo command with its parsed description.
     *
     * @param description the description of the new task.
     */
    public TodoCommand(String description) {
        this(description, new TaskStorage());
    }

    /**
     * Creates a command with a supplied destination for task changes.
     *
     * @param description the command input.
     * @param storage the destination for task changes.
     */
    TodoCommand(String description, TaskStorage storage) {
        super(storage);
        this.description = description;
    }

    /**
     * Validates the description, adds the ToDo, and saves it.
     *
     * @param tasks the current task list.
     * @param ui the UI used to display the result.
     * @throws CaitlynException when the description is empty or saving fails.
     */
    @Override
    public void execute(List<Task> tasks, Ui ui) throws CaitlynException {
        if (description.isEmpty()) {
            throw new CaitlynException(
                    "I beg your pardon, master. I cannot prepare a task without a description.");
        }
        Task task = new Todo(description);
        addTask(tasks, task);
        ui.showTaskAdded(task, tasks.size());
    }
}
