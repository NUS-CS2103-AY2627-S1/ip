package downtowngurl.command;

import downtowngurl.storage.Storage;
import downtowngurl.task.TaskList;
import downtowngurl.ui.Ui;

/**
 * Represents the command that exits the chatbot.
 */
public class ByeCommand extends Command {
    /**
     * Shows the goodbye message.
     *
     * @param tasks Current task list, unused because exiting does not change tasks.
     * @param storage Storage helper, unused because exiting does not change saved data.
     * @param ui UI helper used to show the goodbye message.
     */
    @Override
    public void execute(TaskList tasks, Storage storage, Ui ui) {
        ui.showGoodbye();
    }

    /**
     * Returns true because this command should end the application loop.
     *
     * @return true to signal that the chatbot should exit.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
