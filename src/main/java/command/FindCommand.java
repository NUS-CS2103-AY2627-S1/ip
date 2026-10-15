package command;

import exception.CbtException;
import task.TaskList;

/** Displays tasks whose descriptions contain a keyword. */
public class FindCommand implements Command {
    private final String keyword;

    /**
     * Creates a command using the user-entered search keyword.
     *
     * @param keyword keyword to search for in task descriptions.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns tasks whose descriptions contain the search keyword.
     *
     * @param taskList task list to search.
     * @return result indicating that no application state changed.
     * @throws CbtException if the keyword is blank.
     */
    @Override
    public CommandResult execute(TaskList taskList) throws CbtException {
        if (keyword.isBlank()) {
            throw new CbtException("Use: find KEYWORD (for example, find book)");
        }

        String message = "Matches on the radar:";
        String formattedTasks = taskList.findTasksContaining(keyword).formatTasks();
        if (!formattedTasks.isEmpty()) {
            message += System.lineSeparator() + formattedTasks;
        }
        return new CommandResult(message, false, false);
    }
}
