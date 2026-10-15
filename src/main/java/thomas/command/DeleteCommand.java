package thomas.command;

import thomas.ThomasException;
import thomas.storage.Storage;
import thomas.task.Task;
import thomas.task.TaskList;
import thomas.ui.Ui;

/**
 * Deletes a task from the task list.
 */
public class DeleteCommand extends Command {
    private final String argument;
    private final Parser parser;

    /**
     * Creates a command that deletes a task.
     *
     * @param argument Task number entered by the user.
     * @param parser Parser used to read the task index.
     */
    public DeleteCommand(String argument, Parser parser) {
        this.argument = argument;
        this.parser = parser;
    }

    /**
     * Removes the requested task, saves the list, and confirms the deletion.
     *
     * @param tasks task list to update
     * @param ui user interface used for output
     * @param storage storage used to persist the change
     * @throws ThomasException if the task number is missing or invalid
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ThomasException {
        int taskIndex = parser.getTaskIndex(argument, tasks.size(), "Please specify a task number to delete.");
        Task removedTask = tasks.delete(taskIndex);
        storage.save(tasks);
        ui.showTaskDeleted(removedTask, tasks.size());
    }
}
