package caitlyn;

import java.util.List;
import java.util.Locale;

/**
 * A command that finds tasks whose descriptions contain a keyword.
 */
public final class FindCommand extends Command {
    /** The keyword to search for. */
    private final String keyword;

    /**
     * Creates a find command with its parsed keyword.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public void execute(List<Task> tasks, Ui ui) throws CaitlynException {
        if (keyword.isBlank()) {
            throw new CaitlynException(
                    "I beg your pardon, master. Please provide a keyword, for example: find book.");
        }
        ui.showMatchingTasks(tasks, findMatchingTasks(tasks));
    }

    /**
     * Returns tasks whose descriptions contain the keyword, ignoring letter case.
     *
     * @param tasks the tasks to search.
     * @return matching tasks in their original list order.
     */
    List<Task> findMatchingTasks(List<Task> tasks) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .toList();
    }
}
