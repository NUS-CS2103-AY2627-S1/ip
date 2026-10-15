package zeus.command;

/** Displays every task in the task list. */
public class ListCommand extends Command {
    /** Creates a list command. */
    public ListCommand() {
    }

    /**
     * Displays the tasks without changing or saving them.
     * @param context application components available to the command
     */
    @Override
    public void execute(CommandContext context) {
        context.getUi().showTaskList(context.getTasks().getTasks());
    }
}
