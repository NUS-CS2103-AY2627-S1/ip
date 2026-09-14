package arthur.commands;

import arthur.exceptions.InvalidArgumentException;
import arthur.exceptions.MissingArgumentException;
import arthur.messages.Message;
import arthur.state.CommandContext;

/**
 * A command that handles a task by its index.
 */
public abstract class TaskCommand extends Command {
    // AI-assisted: Named the conversion from the user's one-based task number to a list index.
    private static final int USER_INDEX_OFFSET = 1;
    protected abstract String getArgumentName();

    protected abstract String getCommandName();

    /**
     * Handles a task command.
     *
     * @param context The command context.
     * @param index   The 0-based index of the task to handle.
     * @return The message to display to the user.
     */
    protected abstract Message handle(CommandContext context, int index);

    @Override
    // AI-assisted: Added handling that marks a one-based task number as completed.
    public Message handle(CommandContext context, String arg) {
        if (arg.isEmpty()) {
            throw new MissingArgumentException(getCommandName(), getArgumentName());
        }
        int index;
        try {
            index = Integer.parseInt(arg) - USER_INDEX_OFFSET;
        } catch (NumberFormatException e) {
            throw new InvalidArgumentException(arg);
        }
        if (index < 0 || index >= context.tasks().size()) {
            throw new InvalidArgumentException(arg);
        }
        return handle(context, index);
    }
}
