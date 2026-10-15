package ev.command;

import ev.EvException;
import ev.storage.Storage;
import ev.task.TaskList;
import ev.ui.Ui;

/**
 * Something the user asked EV to do.
 *
 * <p>A command is built by the parser with everything it needs already worked out, so
 * running it is a matter of changing the task list, telling the user what happened, and
 * saving the result if anything changed.
 */
public abstract class Command {

    /**
     * Carries out this command.
     *
     * @param tasks the task list to read or change.
     * @param ui used to tell the user what happened.
     * @param storage used to save the task list when the command changes it.
     * @throws EvException if the command cannot be carried out, or the tasks cannot be saved.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws EvException;

    /**
     * Returns whether EV should stop after this command.
     *
     * @return true only for the command that ends the session.
     */
    public boolean isExit() {
        return false;
    }
}
