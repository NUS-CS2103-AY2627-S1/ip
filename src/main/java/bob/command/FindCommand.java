package bob.command;

import java.util.List;

import bob.ResponseType;
import bob.storage.Storage;
import bob.task.Task;
import bob.task.TaskList;

/**
 * Command that displays tasks with descriptions containing a keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that searches task descriptions for the supplied keyword.
     *
     * @param keyword text to search for
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Finds tasks with descriptions containing the keyword.
     *
     * @param tasks task list to search.
     * @param storage task storage, which is unchanged.
     * @return response containing the numbered matching tasks.
     */
    @Override
    public String execute(TaskList tasks, Storage storage) {
        List<Task> matchingTasks = tasks.find(keyword);
        return formatTasks("Here are the matching missions:", matchingTasks);
    }

    @Override
    public ResponseType getResponseType() {
        return ResponseType.FIND;
    }
}
