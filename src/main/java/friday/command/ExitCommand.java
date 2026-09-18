package friday.command;

import friday.model.TaskList;
import friday.storage.Storage;
import friday.ui.Ui;

/**
 * Ends the chatbot session.
 */
public class ExitCommand extends Command {
    /**
     * Creates an exit command.
     */
    public ExitCommand() {
    }

    /**
     * Displays the farewell message for the current session.
     *
     * @param tasks Current task list, which is unchanged.
     * @param ui UI used to display the farewell.
     * @param storage Storage available to the command, which is unused.
     */
    @Override
    public void execute(TaskList tasks, TaskList archivedTasks, Ui ui, Storage storage, Storage archiveStorage) {
        ui.showExitMessage();
    }

    /**
     * Indicates that this command ends the command loop.
     *
     * @return true because the user requested to exit.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
