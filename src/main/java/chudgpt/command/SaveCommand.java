package chudgpt.command;

import chudgpt.exception.ChudException;
import chudgpt.storage.Storage;
import chudgpt.task.TaskList;
import chudgpt.ui.Ui;

/** Saves the current task list. */
public class SaveCommand extends Command {
    /**
     * Saves the current task list and returns a confirmation message.
     *
     * @param tasks the current task list to save
     * @param ui the UI handler used to create the confirmation
     * @param storage the storage handler used to save the task list
     * @return the response confirming that the tasks were saved
     * @throws ChudException if the task list cannot be saved
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws ChudException {
        storage.save(tasks);
        return ui.getSaveMessage();
    }
}
