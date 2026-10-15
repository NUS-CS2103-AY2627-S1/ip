package elsa.command;

import elsa.storage.Storage;
import elsa.task.TaskList;
import elsa.ui.Ui;

/**
 * Lists how every command is written.
 *
 * <p>The list is not held here. It is asked of {@link CommandType}, which
 * already records how each command is written so that its error messages can
 * show it, so a command added there appears in the help without this class being
 * touched.
 */
public class HelpCommand extends Command {
    /**
     * Creates a command that lists what the chatbot understands.
     * There is nothing to say about the request, so unlike most commands this
     * one is built with no arguments.
     */
    public HelpCommand() {
    }

    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        // Nothing changes, so the list is not saved.
        return ui.getHelpMessage(CommandType.getUsagesByGroup());
    }
}
