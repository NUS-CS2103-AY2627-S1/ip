package windy.command;

/**
 * Displays every task in the task list.
 */
public class ListCommand extends Command {

    /** {@inheritDoc} */
    @Override
    public void execute(CommandContext context) {
        context.getUi().showTaskList(context.getTasks().getTasks());
    }
}
