package walter.command;

import walter.DukeException;
import walter.storage.Storage;
import walter.task.TaskList;
import walter.ui.Ui;

/**
 * Represents a parsed Walter command that can be executed using the application's task list,
 * user interface, and storage components.
 */
public abstract class Command {
    /**
     * Executes this command using Walter's existing application components.
     *
     * @param tasks Task list to inspect or modify.
     * @param ui User interface used to display command results.
     * @param storage Storage used to persist state changes.
     * @throws DukeException If the command cannot be completed or persisted.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws DukeException;

    /**
     * Returns the response category associated with this command.
     *
     * @return Category used by presentation layers; ordinary commands are {@link
     *         CommandCategory#NORMAL} by default.
     */
    public CommandCategory getCategory() {
        return CommandCategory.NORMAL;
    }

    /**
     * Reports whether this command should terminate the application loop.
     *
     * @return {@code true} if Walter should exit after execution; {@code false} otherwise.
     */
    public boolean isExit() {
        return false;
    }
}
