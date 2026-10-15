package sevensix;

import java.util.List;

/** Finds matching descriptions without changing the full list or undo history. */
public final class FindCommand implements Command {
    private final String keyword;

    /** Receives a nonempty, parsed search phrase.
     *
     * @param keyword the phrase to find in task descriptions.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /** Finds the matching tasks and selects their shared response formatter. */
    @Override
    public String execute(TaskList tasks, UndoHistory history) {
        return formatMatches(tasks.find(keyword));
    }

    /** Selects an empty-search message or a numbered result list.
     *
     * @param matches the matching tasks in insertion order.
     * @return the search response.
     */
    private String formatMatches(List<Task> matches) {
        return matches.isEmpty() ? Ui.getNoMatchesMessage() : Ui.formatMatchingTasks(matches);
    }
}
