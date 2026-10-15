package trybot.command;

import trybot.exception.TryBotException;
import trybot.storage.Storage;
import trybot.task.TaskList;
import trybot.ui.Ui;

/**
 * Represents one executable TryBot command.
 */
public abstract class Command {
    /**
     * Executes this command against the current application state.
     *
     * @param tasks current task list.
     * @param ui user-interface handler.
     * @param storage task persistence handler.
     * @throws TryBotException if the command cannot be completed
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws TryBotException;

    /**
     * Indicates whether this command ends the TryBot session.
     *
     * @return true for the exit command, false otherwise
     */
    public abstract boolean isExit();

    /**
     * Saves the task list and reports persistence failures through the UI.
     *
     * @param tasks current task list.
     * @param ui user-interface handler.
     * @param storage task persistence handler.
     */
    protected void saveTasks(TaskList tasks, Ui ui, Storage storage) {
        try {
            storage.saveTasks(tasks.toList());
        } catch (java.io.IOException | SecurityException exception) {
            ui.showSavingError();
        }
    }
}
