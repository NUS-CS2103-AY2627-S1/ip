package thomas.command;

import thomas.ThomasException;
import thomas.storage.Storage;
import thomas.task.TaskList;
import thomas.ui.Ui;

/**
 * Marks a task as not done.
 */
public class UnmarkCommand extends Command {
    private final String argument;
    private final Parser parser;

    /**
     * Creates a command that marks a task as not done.
     *
     * @param argument Task number entered by the user.
     * @param parser Parser used to read the task index.
     */
    public UnmarkCommand(String argument, Parser parser) {
        this.argument = argument;
        this.parser = parser;
    }

    /**
     * Marks the requested task not done, saves the list, and confirms the change.
     *
     * @param tasks task list to update
     * @param ui user interface used for output
     * @param storage storage used to persist the change
     * @throws ThomasException if the task number is missing, invalid, or already not done
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ThomasException {
        int taskIndex = parser.getTaskIndex(argument, tasks.size(), "Please specify a task number to unmark.");
        tasks.unmark(taskIndex);
        storage.save(tasks);
        ui.showTaskUnmarked(tasks.get(taskIndex));
    }
}
