package miku.command;

/** Displays tasks whose descriptions contain a supplied keyword. */
public class FindCommand extends Command {
    private final String keyword;

    /** Creates a command that searches task descriptions for the given keyword. */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /** Displays the matching tasks without changing or saving the task list. */
    @Override
    public String execute(CommandContext commandContext) {
        return commandContext.responseFormatter().formatMatchingTasks(
                commandContext.tasks().findByDescription(keyword));
    }
}
