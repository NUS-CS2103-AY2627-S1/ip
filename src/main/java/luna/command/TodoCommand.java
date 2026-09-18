package luna.command;

import luna.LunaException;
import luna.storage.Storage;
import luna.task.Task;
import luna.task.TaskList;
import luna.task.Todo;
import luna.ui.Ui;

/**
 * Represents the command that adds a todo task.
 */
public class TodoCommand extends Command {
    private final String input;

    /**
     * Creates a todo command from the full user input.
     *
     * @param input Full command entered by the user.
     */
    public TodoCommand(String input) {
        this.input = input;
    }

    /**
     * Parses the todo description, adds the task, saves the list, and shows the result.
     *
     * @param tasks Current tasks in memory.
     * @param ui User interface for showing results.
     * @param storage Storage used to persist task changes.
     * @throws LunaException If the input is incomplete, the description is empty, or saving fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws LunaException {
        if (input.equalsIgnoreCase("todo")) {
            throw new LunaException("Please provide the description for the todo task.");
        }

        String description = input.substring(5).trim();
        if (description.isEmpty()) {
            throw new LunaException("The description of a todo cannot be empty.");
        }

        Task task = new Todo(description);
        addTask(task, tasks, ui, storage);
    }

    /**
     * Returns whether this command changes persisted task data.
     *
     * @return Always {@code true} for a todo command.
     */
    @Override
    public boolean isMutating() {
        return true;
    }
}
