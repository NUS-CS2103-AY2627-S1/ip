package gatsby.command;

import gatsby.model.TaskList;
import gatsby.ui.Ui;

/**
 * Ends the current Gatsby session after displaying the goodbye message.
 */
public class ExitCommand extends Command {
    /** Creates the command that ends the Gatsby session. */
    public ExitCommand() {
    }

    /**
     * Displays the goodbye message. The task list is unused for this command,
     * but is accepted to keep the common command protocol uniform.
     *
     * @param tasks the current task list
     * @param ui the console interaction handler
     */
    @Override
    public void execute(TaskList tasks, Ui ui) {
        ui.printLine(" Bye. Hope to see you again soon!");
    }

    /**
     * Indicates that Gatsby should stop reading input after this command.
     *
     * @return true because this command exits the session
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
