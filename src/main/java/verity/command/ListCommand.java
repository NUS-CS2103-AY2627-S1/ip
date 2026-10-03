package verity.command;

/**
 * Represents a command that displays all tasks.
 */
public class ListCommand extends Command {

    /**
     * Creates a list command.
     */
    public ListCommand() {
        super(false);
    }

    /**
     * Returns all tasks.
     *
     * @param context Command execution context.
     * @return User-facing response after execution.
     */
    @Override
    public String execute(CommandContext context) {
        return context.getUi().getTaskListMessage(context.getTasks());
    }
}
