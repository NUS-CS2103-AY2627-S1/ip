package luna.command;

import luna.LunaException;
import luna.storage.Storage;
import luna.task.Task;
import luna.task.TaskList;
import luna.ui.Ui;

/**
 * Represents the command that marks a task as not done.
 */
public class UnmarkCommand extends Command {
    private final String input;

    /**
     * Creates an unmark command from the full user input.
     *
     * @param input Full command entered by the user.
     */
    public UnmarkCommand(String input) {
        this.input = input;
    }

    /**
     * Marks the task at the requested index as not done, saves the list, and shows the result.
     *
     * @param tasks Current tasks in memory.
     * @param ui User interface for showing results.
     * @param storage Storage used to persist task changes.
     * @throws LunaException If the input is incomplete or saving fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws LunaException {
        int index = parseTaskIndex(input, "unmark", tasks.size(), "unmark");
        Task task = tasks.get(index);
        boolean wasDone = task.isDone();
        task.markAsNotDone();
        try {
            saveTasks(storage, tasks);
        } catch (LunaException e) {
            if (wasDone) {
                task.markAsDone();
            }
            throw e;
        }
        ui.showUnmarkSuccess(task);
    }

    /**
     * Returns whether this command changes persisted task data.
     *
     * @return Always {@code true} for an unmark command.
     */
    @Override
    public boolean isMutating() {
        return true;
    }
}
