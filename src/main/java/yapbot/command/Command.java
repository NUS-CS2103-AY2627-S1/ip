package yapbot.command;

import yapbot.exception.YapBotException;
import yapbot.task.TaskList;
import yapbot.ui.Ui;

/**
 * Represents a user command that can be executed against the task list.
 */
public abstract class Command {

    /**
     * Executes this command.
     *
     * @param taskList the task list to operate on.
     * @param ui the Ui to use for any user-facing output.
     * @throws YapBotException if the command cannot be carried out.
     */
    public abstract void execute(TaskList taskList, Ui ui) throws YapBotException;

    /**
     * Returns whether this command should end the program's main loop.
     *
     * @return {@code true} only for the command that exits the program.
     */
    public boolean isExit() {
        return false;
    }
}