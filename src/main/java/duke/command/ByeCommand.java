package duke.command;

import duke.task.TaskList;
import duke.ui.Ui;

/**
 * Exits the application.
 *
 * <p>Saves the current tasks before showing a goodbye message.</p>
 */
public class ByeCommand extends Command {

    /**
     * Saves tasks and shows the goodbye message.
     *
     * @param tasks The task list to save.
     * @param ui The UI used to show the goodbye message.
     */
    @Override
    public void execute(TaskList tasks, Ui ui) {
        tasks.save();
        ui.showGoodbye();
    }

    /**
     * {@inheritDoc}
     *
     * @return Always {@code true}.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
