package yappa.command;

import yappa.exception.YappaException;
import yappa.task.TaskList;
import yappa.ui.Ui;

/**
 * Represents a command that displays the help menu to the user.
 */
public class HelpCommand extends Command {

    /**
     * Executes the help command by displaying the list of available commands and
     * usage instructions.
     * 
     * @param tasks Task list, which is unchanged.
     * @param ui    User interface used to format the response.
     * @return Formatted goodbye response.
     * @throws YappaException If the response cannot be generated.
     */
    @Override
    public String execute(TaskList tasks, Ui ui) throws YappaException {
        return ui.showHelpMenu();
    }

    /** Returns whether this command modifies the task list. */
    @Override
    public boolean modifiesTasks() {
        return false;
    }
}
