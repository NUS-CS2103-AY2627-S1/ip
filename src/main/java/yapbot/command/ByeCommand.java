package yapbot.command;

import yapbot.task.TaskList;
import yapbot.ui.Ui;

/**
 * Represents the "bye" command, which says goodbye and ends the program.
 */
public class ByeCommand extends Command {

    /**
     * Shows the goodbye message. Does not modify the task list.
     *
     * @param taskList unused; present to satisfy the {@link Command} contract.
     * @param ui the Ui to show the goodbye message on.
     */
    @Override
    public void execute(TaskList taskList, Ui ui) {
        ui.showGoodbye();
    }

    /**
     * {@inheritDoc}
     *
     * @return {@code true}, since "bye" is the command that ends the program.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}