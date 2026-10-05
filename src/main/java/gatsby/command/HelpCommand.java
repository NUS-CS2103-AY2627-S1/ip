package gatsby.command;

import gatsby.model.TaskList;
import gatsby.ui.Ui;

/**
 * Displays the commands that Gatsby understands.
 */
public class HelpCommand extends Command {
    /** Creates the command that displays Gatsby's help text. */
    public HelpCommand() {
    }

    /**
     * Prints a short description of every available command.
     *
     * @param tasks the current task list, unused by this command
     * @param ui the interaction handler used to display the help text
     */
    @Override
    public void execute(TaskList tasks, Ui ui) {
        ui.printLine(" Here are the commands I know:");
        ui.printLine("  todo <description> - add a task");
        ui.printLine("  deadline <description> /by <yyyy-MM-dd HHmm> - add a deadline");
        ui.printLine("  event <description> /from <yyyy-MM-dd HHmm> /to <yyyy-MM-dd HHmm> - add an event");
        ui.printLine("  list - show all tasks");
        ui.printLine("  find <keyword> - find matching tasks");
        ui.printLine("  mark <number> / unmark <number> - update a task's status");
        ui.printLine("  delete <number> - remove a task");
        ui.printLine("  help or ? - show this help");
        ui.printLine("  bye - exit Gatsby");
    }
}
