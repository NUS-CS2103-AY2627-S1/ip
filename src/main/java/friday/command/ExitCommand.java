package friday.command;

import friday.task.TaskList;
import friday.ui.Ui;

/**
 * Represents the command that ends the Friday application.
 */
public class ExitCommand extends Command {
    /**
     * Creates an exit command.
     */
    public ExitCommand() {
        super(Type.BYE, "bye");
    }

    /**
     * Leaves the task list unchanged because the application handles exiting.
     *
     * @param tasks Task list left unchanged.
     * @param ui User interface left unchanged.
     */
    @Override
    public void execute(TaskList tasks, Ui ui) {
        // Friday displays the farewell after observing isExit().
    }

    /**
     * Returns whether this command asks Friday to exit.
     *
     * @return {@code true}, because this is an exit command.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
