package nico;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Finds tasks whose descriptions contain a given keyword.
 */
public class FindCommand extends ParameterCommand {
    /**
     * Creates a command that finds tasks with the given keyword.
     *
     * @param keyword Keyword to search for in task descriptions.
     */
    public FindCommand(String keyword) {
        super("find", keyword);
    }

    @Override
    public void execute(List<Task> tasks, Ui ui) throws NicoException {
        String keyword = getParameter();
        if (keyword == null || keyword.trim().isEmpty()) {
            throw NicoException.invalidInput("Enter a keyword to search for.", "find KEYWORD");
        }

        List<Task> matchingTasks = findKeywordMatches(tasks, keyword.trim());
        ui.showTaskGroup("Found these matching tasks for you lah", matchingTasks);
    }

    /**
     * Returns tasks whose descriptions contain the keyword, regardless of letter case.
     */
    private List<Task> findKeywordMatches(List<Task> tasks, String keyword) {
        List<Task> matchingTasks = new ArrayList<>();
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        for (Task task : tasks) {
            if (task.getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword)) {
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
    }
}
