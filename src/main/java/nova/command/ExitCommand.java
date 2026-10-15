package nova.command;

import nova.task.TaskList;
import nova.ui.Ui;

/**
 * Ends the current Nova session.
 */
public class ExitCommand extends Command {
    /**
     * Creates a command that exits Nova.
     */
    public ExitCommand() {
    }

    /**
     * Displays Nova's farewell message.
     *
     * @param tasks task list; not used by this command
     * @param ui UI used to display the farewell message
     */
    @Override
    public void execute(TaskList tasks, Ui ui) {
        ui.showGoodbye();
    }

    /**
     * Returns whether this command exits Nova.
     *
     * @return {@code true}
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
