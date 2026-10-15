package nano.command;

import nano.Storage;
import nano.task.Task;
import nano.task.TaskList;

/**
 * Represents the find command.
 */
public class FindCommand implements Command {
    private final String keyword;

    /**
     * Creates a find command for the given keyword.
     *
     * @param keyword keyword to search for
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String execute(TaskList tasks, Storage storage) {
        TaskList matchingTasks = tasks.find(keyword);

        StringBuilder response = new StringBuilder("Here are the matching tasks:");

        for (int i = 0; i < matchingTasks.size(); i++) {
            Task task = matchingTasks.get(i);
            response.append("\n")
                    .append(i + 1)
                    .append(".")
                    .append(task);
        }

        return response.toString();
    }
}
