package chudgpt.command;

import chudgpt.storage.Storage;
import chudgpt.task.TaskList;
import chudgpt.ui.Ui;

/** Displays a greeting from ChudGPT. */
public class HiCommand extends Command {
    /**
     * Returns the greeting created by the UI handler.
     *
     * @param tasks the current task list, which is not modified
     * @param ui the UI handler used to create the greeting
     * @param storage the storage handler, which is not used by this command
     * @return the greeting response
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        return ui.getGreetingMessage();
    }
}
