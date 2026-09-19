package nyonbot.command;

import java.util.HashMap;

import nyonbot.Logic.Result;
import nyonbot.model.NyonException;
import nyonbot.model.Task;
import nyonbot.model.TaskList;

/**
 * Finds and returns all tasks matching an input keyword(s).
 */
public class FindCommand extends Command {
    private TaskList list;

    /**
     * Creates a FindCommand with parsed arguments and a TaskList to search.
     * @param arguments parsed command arguments.
     * @param task      the TaskList to search through.
     */
    public FindCommand(HashMap<String, String> arguments, TaskList tasks) {
        super(arguments);
        this.list = tasks;
    }

    /** {@inheritDoc} */
    @Override
    public Result execute() throws NyonException {
        String searchInput = arguments.get(DESCRIPTION_KEY);
        if (searchInput == null || searchInput.isBlank()) {
            throw new NyonException("specify a search string");
        }

        String[] searchTerms = searchInput.trim().split("\\s+");
        TaskList matchingTasks = new TaskList();

        for (Task task : list) {
            if (matchesAnySearchTerm(task, searchTerms)) {
                matchingTasks.add(task);
            }
        }

        if (matchingTasks.isEmpty()) {
            return new Result("couldn't find anything matching the search string...");
        }
        return new ListCommand(arguments, matchingTasks).execute();
    }

    private boolean matchesAnySearchTerm(Task task, String[] searchTerms) {
        String taskName = task.getName();

        for (String term : searchTerms) {
            if (taskName.indexOf(term) != -1) {
                return true;
            }
        }
        return false;
    }
}
