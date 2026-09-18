package luna.command;

import luna.LunaException;
import luna.storage.Storage;
import luna.task.Task;
import luna.task.TaskList;
import luna.ui.Ui;

/**
 * Represents the command that deletes a task.
 */
public class DeleteCommand extends Command {
    private final String input;

    /**
     * Creates a delete command from the full user input.
     *
     * @param input Full command entered by the user.
     */
    public DeleteCommand(String input) {
        this.input = input;
    }

    /**
     * Deletes the task at the requested index, saves the list, and shows the result.
     *
     * @param tasks Current tasks in memory.
     * @param ui User interface for showing results.
     * @param storage Storage used to persist task changes.
     * @throws LunaException If the input is incomplete or saving fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws LunaException {
        int index = parseTaskIndex(input, "delete", tasks.size(), "delete");
        Task task = tasks.get(index);
        TaskList updatedTasks = new TaskList(tasks.asList());
        updatedTasks.remove(index);
        saveTasks(storage, updatedTasks);
        tasks.remove(index);
        ui.showDeleteSuccess(task, tasks.size());
    }

    /**
     * Returns whether this command changes persisted task data.
     *
     * @return Always {@code true} for a delete command.
     */
    @Override
    public boolean isMutating() {
        return true;
    }
}
