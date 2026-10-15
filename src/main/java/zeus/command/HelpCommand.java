package zeus.command;

/** Displays guidance for all commands supported by Zeus. */
public class HelpCommand extends Command {
    /** Creates a help command. */
    public HelpCommand() {
    }

    /**
     * Displays command guidance without changing or saving tasks.
     *
     * @param context Application components available to the command.
     */
    @Override
    public void execute(CommandContext context) {
        context.getUi().showHelp();
    }
}
