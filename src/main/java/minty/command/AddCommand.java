package minty.command;

import minty.exception.MintyException;
import minty.storage.Storage;
import minty.task.Task;
import minty.task.TaskList;
import minty.ui.Ui;

/**
 * Adds one parsed task, persists the updated list, and confirms the change.
 */
public abstract class AddCommand extends Command {
    private final String fullCommand;

    /**
     * Creates an add command from the user's complete input.
     *
     * @param fullCommand complete command entered by the user.
     */
    protected AddCommand(String fullCommand) {
        this.fullCommand = fullCommand;
    }

    /**
     * Parses, adds, saves, and displays the new task.
     *
     * @param tasks task list to update.
     * @param ui command-line interface used for the response.
     * @param storage destination for updated task data.
     * @throws MintyException if the task details are invalid.
     */
    @Override
    public final void execute(TaskList tasks, Ui ui, Storage storage) throws MintyException {
        Task task = createTask(fullCommand);
        for (Task existing : tasks) {
            if (existing.hasSameDetails(task)) {
                throw new MintyException("That task is already on your list! Use list to find it.");
            }
        }
        tasks.add(task);
        saveTasks(tasks, ui, storage);
        ui.showTask("Fresh task coming right up! I've added:", task);
        ui.showTaskCount(tasks.size());
    }

    /**
     * Creates the specific task represented by the complete command.
     *
     * @param fullCommand complete command entered by the user.
     * @return validated task.
     * @throws MintyException if the task details are invalid.
     */
    protected abstract Task createTask(String fullCommand) throws MintyException;
}
