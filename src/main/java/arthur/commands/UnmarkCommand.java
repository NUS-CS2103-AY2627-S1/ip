package arthur.commands;

import arthur.messages.Message;
import arthur.messages.UnmarkMessage;
import arthur.state.CommandContext;
import arthur.tasks.Task;

/**
 * Marks a numbered task as not completed.
 */
public class UnmarkCommand extends TaskCommand {
    private static final String COMMAND_NAME = "unmark";
    private static final String ARGUMENT_NAME = "index";

    @Override
    protected String getArgumentName() {
        return ARGUMENT_NAME;
    }

    @Override
    protected String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    protected Message handle(CommandContext context, int index) {
        Task task = context.tasks().get(index);
        task.unmark();
        return new UnmarkMessage(task);
    }
}
