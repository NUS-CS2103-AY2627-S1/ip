package dulio.command;

import java.io.IOException;

import dulio.task.TaskList;
import dulio.ui.Ui;

/**
 * Ends the Dulio session.
 */
public class ExitCommand extends Command {
    /**
     * Creates an exit command.
     */
    public ExitCommand() {
    }

    /**
     * Displays the goodbye message.
     *
     * @param tasks Unused task list.
     * @param ui The interface used for the response.
     * @throws IOException If persistence fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui) throws IOException {
        ui.showGoodbye();
    }

    /**
     * Returns true because this command ends the session.
     *
     * @return True if this command exits.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
