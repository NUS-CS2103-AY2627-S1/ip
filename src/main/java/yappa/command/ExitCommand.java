package yappa.command;

import yappa.exception.YappaException;
import yappa.task.TaskList;
import yappa.ui.Ui;

/** Requests that the application exit. */
public class ExitCommand extends Command {
    /**
     * Returns the goodbye response.
     *
     * @param tasks Task list, which is unchanged.
     * @param ui    User interface used to format the response.
     * @return Formatted goodbye response.
     * @throws YappaException If the response cannot be generated.
     */
    @Override
    public String execute(
            TaskList tasks, Ui ui)
            throws YappaException {

        return ui.showGoodbye();
    }

    /** Returns whether this command modifies the task list. */
    @Override
    public boolean modifiesTasks() {
        return false;
    }

    /** Returns whether this command requests application exit. */
    @Override
    public boolean isExit() {
        return true;
    }
}
