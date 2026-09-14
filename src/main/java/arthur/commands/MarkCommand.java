package arthur.commands;

import arthur.messages.MarkMessage;
import arthur.messages.Message;
import arthur.state.CommandContext;
import arthur.tasks.Task;

/**
 * Marks a numbered task as completed.
 */
public class MarkCommand extends TaskCommand {
    private static final String COMMAND_NAME = "mark";
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
        task.mark();
        return new MarkMessage(task);
    }
}
