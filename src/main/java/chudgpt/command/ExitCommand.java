package chudgpt.command;

import chudgpt.exception.ChudException;
import chudgpt.storage.Storage;
import chudgpt.task.TaskList;
import chudgpt.ui.Ui;

/** Saves the current task list and exits the application. */
public class ExitCommand extends Command {
    @Override
    public boolean isExit() {
        return true;
    }

    /**
     * Saves the current task list and returns the exit message.
     *
     * @param tasks the current task list
     * @param ui the UI handler used to create the exit message
     * @param storage the storage handler used to save the task list
     * @return the goodbye response
     * @throws ChudException if the task list cannot be saved.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws ChudException {
        storage.save(tasks);
        return ui.getGoodbyeMessage();
    }
}
