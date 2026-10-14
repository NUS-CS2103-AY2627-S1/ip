package verity.command;

import java.util.List;

import verity.storage.Storage;
import verity.task.Task;
import verity.task.TaskList;

/**
 * Lists every task whose description contains a given keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that finds tasks matching the given keyword.
     *
     * @param keyword Keyword to search for.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Lists every task whose description contains this command's keyword.
     *
     * @param tasks {@inheritDoc}
     * @param storage {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public String execute(TaskList tasks, Storage storage) {
        List<Task> matches = tasks.findByKeyword(keyword);
        if (matches.isEmpty()) {
            return "Nothing matches. Try spelling it right.";
        }
        return "Found these - you're welcome:\n" + formatNumberedList(matches);
    }
}
