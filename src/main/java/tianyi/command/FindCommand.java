package tianyi.command;

import java.util.List;
import java.util.stream.Collectors;

import tianyi.Response;
import tianyi.TianyiException;
import tianyi.storage.Storage;
import tianyi.task.IndexedTask;
import tianyi.task.TaskList;

/**
 * Finds and displays tasks whose descriptions contain a keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that searches task descriptions for the specified keyword.
     *
     * @param keyword Keyword to find in task descriptions.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns tasks whose descriptions contain the command keyword.
     *
     * @param tasks Task list to search.
     * @param storage Storage instance, which is not used.
     * @return Formatted tasks whose descriptions contain the keyword.
     * @throws TianyiException If the task search cannot be completed.
     */
    @Override
    public Response execute(TaskList tasks, Storage storage)
            throws TianyiException {
        List<IndexedTask> matches = tasks.listTasks(keyword);

        if (matches.isEmpty()) {
            return new Response("", "I couldn't find a match. Try another keyword?");
        }

        String message = matches.stream()
                .map(IndexedTask::toString)
                .collect(Collectors.joining("\n"));

        return new Response("Here's what I found for you:", message);
    }
}
