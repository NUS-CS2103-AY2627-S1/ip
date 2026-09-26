package dulio.command;

import java.io.IOException;

import dulio.task.TaskList;
import dulio.ui.Ui;

/**
 * Represents a parsed command that can be executed by Dulio.
 */
public abstract class Command {
    /**
     * Creates a command.
     */
    protected Command() {
    }

    /**
     * Executes this command using the task list and user interface.
     *
     * @param tasks The task list to modify or display.
     * @param ui The interface used for responses.
     * @throws IOException If persistence fails.
     */
    public abstract void execute(TaskList tasks, Ui ui) throws IOException;

    /**
     * Returns whether this command ends the application.
     *
     * @return True when the application should exit.
     */
    public boolean isExit() {
        return false;
    }
}
