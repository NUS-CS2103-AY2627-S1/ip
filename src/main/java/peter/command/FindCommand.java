package peter.command;

import java.util.List;

import peter.storage.Storage;
import peter.task.TaskList;
import peter.ui.Ui;

/**
 * Displays the tasks whose descriptions contain every one of several keywords.
 */
public class FindCommand extends Command {
    private final List<String> keywords;

    /**
     * Creates a command that reports the tasks matching every keyword.
     *
     * @param keywords keywords to search descriptions for.
     */
    public FindCommand(List<String> keywords) {
        // An empty list would make the search match every task, since a test
        // over no keywords is trivially true. The parser rejects a blank
        // search before this point, so an empty list is a programming error.
        assert !keywords.isEmpty() : "a find command must have at least one keyword";

        this.keywords = List.copyOf(keywords);
    }

    public List<String> getKeywords() {
        return keywords;
    }

    /**
     * {@inheritDoc}
     *
     * <p>A task matches only when every keyword appears somewhere in its
     * description, so extra keywords narrow the results. The keywords may be
     * given in any order and need not be adjacent in the description, and each
     * one matches any substring, so {@code find read book} also finds
     * {@code read a book}.
     *
     * <p>Reads the task list without changing it, so nothing is saved and no
     * rollback is needed.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMatchingTasks(tasks, task -> keywords.stream().allMatch(task::hasKeyword));
    }
}
