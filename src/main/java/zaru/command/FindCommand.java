package zaru.command;

import java.util.List;

import zaru.exception.ZaruException;
import zaru.task.Task;
import zaru.task.TaskList;

/** Executes the {@code find} command. */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a find command.
     *
     * @param keyword Keyword entered by the user for the search.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the command word represented by this command.
     *
     * @return The {@code find} command word.
     */
    @Override
    public String getCommand() {
        return "find";
    }

    /**
     * Finds tasks whose titles contain the requested search text and returns the response message.
     *
     * @param tasks Current task list.
     * @return Matching-task response message.
     * @throws ZaruException If the search text is empty.
     */
    @Override
    public String execute(TaskList tasks) throws ZaruException {
        validateNonEmpty(keyword, "Include a search target!");
        List<Task> matchingTasks = tasks.filterByTitle(keyword);
        StringBuilder matchingTasksText = new StringBuilder();

        for (Task task : matchingTasks) {
            matchingTasksText.append("   ").append(task).append("\n");
        }

        return "Meow! Here are the matching tasks in your list:\n%s".formatted(matchingTasksText);
    }
}
