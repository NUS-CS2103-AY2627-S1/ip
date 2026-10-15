package nova.command;

import nova.task.TaskList;
import nova.ui.Ui;

/**
 * Displays the available Nova commands and their syntax.
 */
public class HelpCommand extends Command {
    /**
     * Creates a command that displays help information.
     */
    public HelpCommand() {
    }

    /**
     * Displays the available Nova commands and their syntax.
     *
     * @param tasks task list; not used by this command
     * @param ui UI used to display the help information
     */
    @Override
    public void execute(TaskList tasks, Ui ui) {
        ui.showHelp();
    }
}
