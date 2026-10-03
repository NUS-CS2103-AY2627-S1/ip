package verity.command;

/**
 * Represents a command that displays the command guide.
 */
public class HelpCommand extends Command {

    /**
     * Creates a help command.
     */
    public HelpCommand() {
        super(false);
    }

    /**
     * Returns the command guide without changing stored data.
     *
     * @param context Command execution context.
     * @return User-facing command guide.
     */
    @Override
    public String execute(CommandContext context) {
        return context.getUi().getHelpMessage();
    }
}
