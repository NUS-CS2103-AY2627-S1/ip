package zeus.command;

/** Ends the current Zeus session. */
public class ExitCommand extends Command {
    /** Creates an exit command. */
    public ExitCommand() {
    }

    /**
     * Displays Zeus's farewell message.
     * @param context application components available to the command
     */
    @Override
    public void execute(CommandContext context) {
        context.getUi().showGoodbye();
    }

    /**
     * Signals that the command loop should stop.
     * @return true
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
