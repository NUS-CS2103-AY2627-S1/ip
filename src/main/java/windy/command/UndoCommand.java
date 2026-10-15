package windy.command;

/** Reverts the latest task change in the current session. */
public class UndoCommand extends Command {
    /** {@inheritDoc} */
    @Override
    public void execute(CommandContext context) {
        context.restoreTaskChange(true);
    }
}
