package verity.command;

/**
 * Represents a command that exits the chatbot.
 */
public class ExitCommand extends Command {

    /**
     * Creates an exit command.
     */
    public ExitCommand() {
        super(true);
    }

    /**
     * Returns Verity's exit message.
     *
     * @param context Command execution context.
     * @return Exit message.
     */
    @Override
    public String execute(CommandContext context) {
        return context.getUi().getExitMessage();
    }
}
