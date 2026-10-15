package charlie.command;

import charlie.storage.Storage;
import charlie.task.TaskList;
import charlie.ui.Ui;

/**
 * Represents an executable user command.
 */
public abstract class Command {
    /**
     * Creates a command.
     */
    public Command() {
    }

    /**
     * Performs this command using Charlie's application collaborators.
     *
     * @param tasks Task collection on which the command operates.
     * @param ui User interface used to display command results.
     * @param storage Storage used to persist task changes.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage);

    /**
     * Returns whether this command should end the command loop.
     *
     * @return True when Charlie should exit after executing this command.
     */
    public boolean isExit() {
        return false;
    }
}
