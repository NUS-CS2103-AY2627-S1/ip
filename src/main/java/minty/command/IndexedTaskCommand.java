package minty.command;

import minty.exception.MintyException;
import minty.storage.Storage;
import minty.task.Task;
import minty.task.TaskList;
import minty.ui.Ui;

/**
 * Updates one task selected by its one-based command-line number.
 */
public abstract class IndexedTaskCommand extends Command {
    private final String fullCommand;
    private final CommandType commandType;

    /**
     * Creates an indexed command from its complete input and recognized type.
     *
     * @param fullCommand complete command entered by the user.
     * @param commandType recognized indexed command type.
     */
    protected IndexedTaskCommand(String fullCommand, CommandType commandType) {
        this.fullCommand = fullCommand;
        this.commandType = commandType;
    }

    /**
     * Validates the task number, performs the update, saves, and confirms it.
     *
     * @param tasks task list to update.
     * @param ui command-line interface used for the response.
     * @param storage destination for updated task data.
     * @throws MintyException if the task number is invalid.
     */
    @Override
    public final void execute(TaskList tasks, Ui ui, Storage storage) throws MintyException {
        int taskIndex = Parser.parseTaskIndex(fullCommand, commandType, tasks.size());
        Task task = updateTask(tasks, taskIndex);
        saveTasks(tasks, ui, storage);
        ui.showTask(getConfirmationMessage(), task);
        showAdditionalResponse(tasks, ui);
    }

    /**
     * Performs the command-specific update on a validated task index.
     *
     * @param tasks task list to update.
     * @param taskIndex validated zero-based task index.
     * @return task affected by the command.
     */
    protected abstract Task updateTask(TaskList tasks, int taskIndex);

    /**
     * Returns the command-specific confirmation shown above the affected task.
     *
     * @return confirmation message.
     */
    protected abstract String getConfirmationMessage();

    /**
     * Shows any command-specific response needed after the affected task.
     *
     * @param tasks updated task list.
     * @param ui command-line interface used for the response.
     */
    protected void showAdditionalResponse(TaskList tasks, Ui ui) {
        // Most indexed commands need no additional response.
    }
}
