package zeus.command;

/**
 * Displays tasks whose descriptions contain a keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that searches for the specified keyword.
     *
     * @param keyword Keyword to find in task descriptions.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Displays matching tasks without changing or saving them.
     *
     * @param context Application components available to the command.
     */
    @Override
    public void execute(CommandContext context) {
        context.getUi().showMatchingTasks(context.getTasks().find(keyword));
    }
}
