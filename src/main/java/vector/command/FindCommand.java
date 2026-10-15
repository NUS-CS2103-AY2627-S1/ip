package vector.command;

import java.util.ArrayList;

import vector.storage.Storage;
import vector.task.Task;
import vector.task.TaskList;
import vector.ui.Ui;

/**
 * Represents a command to find tasks by searching for a keyword.
 */
public class FindCommand extends Command {
    private String keyword;

    /**
     * Constructs a FindCommand with the specified keyword.
     *
     * @param keyword The keyword to search for in task descriptions.
     * @return The response string.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        ArrayList<Task> matchingTasks = tasks.findTasks(keyword);
        if (matchingTasks.isEmpty()) {
            return "No matching tasks found.";
        }

        String taskListStr = java.util.stream.IntStream.range(0, matchingTasks.size())
                .mapToObj(i -> (i + 1) + "." + matchingTasks.get(i).toString())
                .collect(java.util.stream.Collectors.joining("\n"));
        return "Search protocol complete. Query results:\n" + taskListStr;
    }
}
