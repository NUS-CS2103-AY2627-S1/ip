package lumine.command;

import lumine.task.TaskList;
import lumine.ui.Ui;

/**
 * Exits the application.
 */
public class ExitCommand extends Command {

    /**
     * {@inheritDoc}
     * Displays the farewell message before the application shuts down.
     */
    @Override
    public void execute(TaskList taskList, Ui ui) {
        ui.showExit();
    }

    /**
     * {@inheritDoc}
     * Returns {@code true} to signal the main loop to stop.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
