package windy.command;

/** Restores the latest task change in the current session. */
public class RedoCommand extends Command {
    /** {@inheritDoc} */
    @Override
    public void execute(CommandContext context) {
        context.restoreTaskChange(false);
    }
}
