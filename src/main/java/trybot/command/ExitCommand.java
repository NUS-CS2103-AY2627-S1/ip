package trybot.command;

import trybot.storage.Storage;
import trybot.task.TaskList;
import trybot.ui.Ui;

/**
 * Ends the TryBot session.
 */
public class ExitCommand extends Command {
    /**
     * Displays the goodbye message for the current session.
     *
     * @param tasks current task list, unused.
     * @param ui user-interface handler.
     * @param storage task persistence handler, unused because exiting makes no changes.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbyeMessage();
    }

    /**
     * Indicates that this command ends the session.
     *
     * @return always true
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
