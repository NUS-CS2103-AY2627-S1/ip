package elsa.command;

import elsa.storage.Storage;
import elsa.task.TaskList;
import elsa.ui.Ui;

/**
 * Says goodbye and ends the session.
 *
 * <p>This is the one command that answers isExit() with true. The chatbot's loop
 * asks every command that question and stops when one says yes, so leaving is
 * decided by the command itself rather than by the loop recognising "bye".
 */
public class ExitCommand extends Command {
    /**
     * Creates a command that ends the session.
     * Leaving needs nothing said about it, so unlike most commands this one is
     * built with no arguments.
     */
    public ExitCommand() {
    }

    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        // The list is saved after every change, so there is nothing left to write.
        return ui.getFarewellMessage();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
