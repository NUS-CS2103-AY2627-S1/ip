package shan.command;

import java.util.List;

import shan.storage.Storage;
import shan.task.Task;
import shan.task.TaskList;

/**
 * Displays tasks whose descriptions contain a keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Constructs a command that searches task descriptions.
     *
     * @param keyword Keyword to find.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String execute(TaskList tasks, Storage storage) {
        List<Task> matchingTasks = tasks.find(this.keyword);
        StringBuilder result = new StringBuilder(
                "Here are the matching tasks in your list:");
        for (int index = 0; index < matchingTasks.size(); index++) {
            result.append(String.format("\n%d.%s", index + 1, matchingTasks.get(index)));
        }
        return result.toString();
    }
}
