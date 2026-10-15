package windy.command;

import java.util.List;

import windy.task.Task;

/**
 * Displays tasks whose descriptions contain a keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that searches task descriptions for a keyword.
     *
     * @param keyword the keyword to search for.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(CommandContext context) {
        List<Task> foundTasks = context.getTasks().findTasksContainingKeyword(keyword);
        context.getUi().showFoundTasks(foundTasks, "No tasks match '" + keyword + "'.");
    }
}
