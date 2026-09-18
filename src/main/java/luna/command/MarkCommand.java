package luna.command;

import luna.LunaException;
import luna.storage.Storage;
import luna.task.Task;
import luna.task.TaskList;
import luna.ui.Ui;

/**
 * Represents the command that marks a task as done.
 */
public class MarkCommand extends Command {
    private final String input;

    /**
     * Creates a mark command from the full user input.
     *
     * @param input Full command entered by the user.
     */
    public MarkCommand(String input) {
        this.input = input;
    }

    /**
     * Marks the task at the requested index as done, saves the list, and shows the result.
     *
     * @param tasks Current tasks in memory.
     * @param ui User interface for showing results.
     * @param storage Storage used to persist task changes.
     * @throws LunaException If the input is incomplete or saving fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws LunaException {
        int index = parseTaskIndex(input, "mark", tasks.size(), "mark");
        Task task = tasks.get(index);
        boolean wasDone = task.isDone();
        task.markAsDone();
        try {
            saveTasks(storage, tasks);
        } catch (LunaException e) {
            if (!wasDone) {
                task.markAsNotDone();
            }
            throw e;
        }
        ui.showMarkSuccess(task);
    }

    /**
     * Returns whether this command changes persisted task data.
     *
     * @return Always {@code true} for a mark command.
     */
    @Override
    public boolean isMutating() {
        return true;
    }
}
