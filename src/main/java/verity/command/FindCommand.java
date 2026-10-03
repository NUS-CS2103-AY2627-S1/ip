package verity.command;

import java.util.List;

import verity.task.Task;

/**
 * Represents a command that finds tasks by a description keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a keyword-search command.
     *
     * @param keyword Keyword to search for.
     */
    public FindCommand(String keyword) {
        super(false);
        this.keyword = keyword;
    }

    /**
     * Finds and returns tasks whose descriptions contain the keyword.
     *
     * @param context Command execution context.
     * @return User-facing response after execution.
     */
    @Override
    public String execute(CommandContext context) {
        List<Task> matchingTasks =
                context.getTasks().findByKeyword(keyword);
        return context.getUi().getMatchingTasksMessage(matchingTasks);
    }
}
