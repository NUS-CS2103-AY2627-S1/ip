package bob.command;

import java.util.List;

import bob.ResponseType;
import bob.storage.Storage;
import bob.task.Task;
import bob.task.TaskList;

/**
 * Command that displays tasks matching every word-prefix term in a query.
 */
public class SearchCommand extends Command {
    private final String query;

    /**
     * Creates a command that searches task descriptions using the supplied query.
     *
     * @param query whitespace-separated word prefixes.
     */
    public SearchCommand(String query) {
        this.query = query;
    }

    /**
     * Searches task descriptions and formats the matching tasks.
     *
     * @param tasks task list to search.
     * @param storage task storage, which is unchanged.
     * @return response containing matching tasks or a no-match message.
     */
    @Override
    public String execute(TaskList tasks, Storage storage) {
        List<Task> matchingTasks = tasks.search(query);
        if (matchingTasks.isEmpty()) {
            return "I couldn't find any matching missions.";
        }

        StringBuilder response = new StringBuilder("Here are the matching missions:");
        List<Task> allTasks = tasks.getTasks();
        for (Task matchingTask : matchingTasks) {
            response.append('\n')
                    .append(allTasks.indexOf(matchingTask) + 1)
                    .append('.')
                    .append(matchingTask);
        }
        return response.toString();
    }

    @Override
    public ResponseType getResponseType() {
        return ResponseType.FIND;
    }
}
